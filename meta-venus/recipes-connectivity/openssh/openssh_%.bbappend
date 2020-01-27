FILESEXTRAPATHS_prepend := "${THISDIR}:"

inherit daemontools

DAEMON_PN = "${PN}-sshd"
DAEMONTOOLS_SERVICE_DIR = "/etc/ssh/service"
DAEMONTOOLS_RUN = "${bindir}/start-sshd.sh"
DAEMONTOOLS_LOG_DIR = "${DAEMONTOOLS_LOG_DIR_PREFIX}/sshd"

SRC_URI += "file://start-sshd.sh"

do_install_append() {
    install -m 755 ${WORKDIR}/start-sshd.sh ${D}${bindir}
}

# Victron disable auto startup of sshd, but we want it
# Note that this IGNORES victron's start-sshd.sh
## disable the update-rc.d
## INITSCRIPT_PACKAGES = ""
