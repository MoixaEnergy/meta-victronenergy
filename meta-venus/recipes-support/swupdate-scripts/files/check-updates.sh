#!/bin/bash
# Moixa check-update removing remote updates
# 
# RESUME DOWNLOAD DETAILS
# swupdate can retry and resume a broken download. See -t and -r arguments
# in do_swupdate call at end of this file.
#
# Implementation in swupdate:
# https://github.com/sbabic/swupdate/blob/master/corelib/downloader.c
#
# Note that it only resumes the download when kept running: after the
# swupdate process has stopped, for example because of a reboot, it will
# restart. Improving this, without adding an intermediate scratchpad on disk,
# is not straightforward: the download is streamed straight into ubifs on the
# CCGX, and re-opening an unfinished ubifs volume is not a good idea.
#
# Resuming while online file has changed:
# When a new version is made available, the venus-swu-[machine].swu file on
# the webserver is replaced with the newer one. Devices busy starting a
# resume after that should not accidentally resume the download with the new
# file. A waste of bandwidth and possible leads to installing and booting
# into a corrupt rootfs. (What type of CRC or hash does swupdate on the swu
# file after download?)
#
# This is prevented this by dowloading the file that contains its version
# in the name. Therefore the webserver should always have the latest file
# available under two names:
# venus-swu-[machine].swu
# venus-swu-[machine]-[build-date-time].swu
#
# Best sequence of installing the files on the webserver is:
# 1. venus-swu-[machine]-[build-date-time].swu
# 2. venus-swu-[machine].swu
# 3. rename or remove the old build-date-time file: force the running
#    downloads to cancel.

. $(dirname $0)/functions.sh

get_setting() {
    dbus-send --print-reply=literal --system --type=method_call \
              --dest=com.victronenergy.settings \
              "/Settings/System/$1" \
              com.victronenergy.BusItem.GetValue |
        awk '{ print $3 }'
}

get_swu_version() {
    if [ -f "$1" ]; then
        # local file
        cmd="head -n 10"
    else
        # url, probably
        cmd="curl -s -r 0-999 -m 30 --retry 3"
    fi

    $cmd "$1" |
        cpio --quiet -i --to-stdout sw-description 2>/dev/null |
        sed -n '/venus-version/ {
            s/.*"\(.*\)".*/\1/
            p
            q
        }'
}

swu_status() {
    printf '%s\n' "$1" ${offline:+""} "$2" >$status_file
}

status_file=/var/run/swupdate-status

start_log

echo "*** Checking for updates ***"
echo "arguments: $@"

while [[ $# -gt 0 ]]; do
    case "$1" in
        -swu)
                 shift
                 force=y
                 forceswu="$1"
                 update="1"
        ;;
        -offline)offline=y   ;;
        -help)   help=y      ;;
        *)       echo "Invalid option $arg"
                 exit 1
                 ;;
    esac
    shift
done

if [ "$help" = y ]; then
    echo "check-updates.sh: wrapper script around swupdate"
    echo
    echo "Arguments:"
    echo "-swu url forcefully install the swu from given url"
    echo "-offline search for updates on removable storage devices"
    echo "-help    this help"
    exit
fi

machine=$(cat /etc/venus/machine)

if [[ $forceswu ]]; then
    echo "Updating to $forceswu"
    SWU="$forceswu"
    # The version is not known, since the stream might not support seeking,
    # like stdin for example. So as a best effort, use the url instead.
    swu_version="$forceswu"
elif [ "$offline" = y ]; then
    echo "Searching for update on SD/USB..."

    for dev in /media/*; do
        # reverse order gives preference to an unversioned file
        # followed by that with the most recent timestamp if
        # multiple files exist
        #
        # MIND IT: There are ccgx and venusgx around which only check for
        # venus-swu-${machine}*.swu so don't make an incompatible ccgxv2 or
        # beaglebone-new MACHINE, since they are also accepted by the old ones.
        SWU=$(ls -r $dev/venus-swu-${machine}-*.swu $dev/venus-swu-${machine}.swu 2>/dev/null | head -n1)
        test -f "$SWU" && break
    done

    if [ -f "$SWU" ]; then
        echo "Update found on $dev"
        feed="$dev"
    else
        echo "Update not found. Exit."
        swu_status -3
        exit 1
    fi
fi

if [[ -z $forceswu ]]; then
    echo "Retrieving latest version (feed=$feed)..."
    swu_status 1

    cur_version=$(get_version)
    swu_version=$(get_swu_version "$SWU")

    if [ -z "$swu_version" ]; then
        echo "Unable to retrieve latest software version, exit."
        swu_status -1
        exit 1
    fi

    cur_build=${cur_version%% *}
    swu_build=${swu_version%% *}

    echo "installed: $cur_version"
    echo "available: $swu_version"

    if [ "$force" != y -a "${swu_build}" -le "${cur_build}" ]; then
        echo "No newer version available, exit."
        swu_status 0
        exit
    fi

    if [ "$update" != 1 ]; then
        swu_status 0 "$swu_version"
        exit
    fi
fi

altroot=$(get_altrootfs)

if [ -z "$altroot" ]; then
    echo "Unable to determine rootfs. Exit."
    swu_status -2 "$swu_version"
    exit 1
fi

if ! lock; then
    echo "Can't get lock, other process already running? Exit."
    swu_status 0 "$swu_version"
    exit
fi

echo "Starting swupdate to install version $swu_version ..."
swu_status 2 "$swu_version"

# backup rootfs is about to be replaced, remove its version entry
get_version >/var/run/versions

if [ -f "$SWU" ]; then
    swupdate_flags="-i"
else
    swupdate_flags="-t 30 -r 3 -d"
fi

if do_swupdate -v $swupdate_flags "$SWU" -e "stable,copy$altroot"; then
    echo "do_swupdate completed OK. Rebooting"
    swu_status 3 "$swu_version"
    reboot
else
    echo "Error, do_swupdate stopped with exitcode $?, unlock and exit."
    swu_status -2 "$swu_version"
fi

unlock
