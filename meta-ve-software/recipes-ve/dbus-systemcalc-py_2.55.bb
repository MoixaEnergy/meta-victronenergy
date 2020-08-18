DESCRIPTION = "VE system calculations"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9b0a9609befce3122afcc444da0fe825"

inherit ve_package
inherit daemontools
inherit python-compile

SRC_URI = " \
    gitsm://github.com/victronenergy/dbus-systemcalc-py.git;protocol=https;branch=p3 \
    file://com.victronenergy.system.conf \
"
SRCREV = "95272bc3c972a67062e895254164851309b68f33"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = " \
    localsettings \
    python3-core \
    python3-dbus \
    python3-pprint \
    python3-pygobject \
"

DAEMONTOOLS_SERVICE_DIR = "${bindir}/service"
DAEMONTOOLS_RUN = "softlimit -d 100000000 -s 1000000 -a 100000000 ${bindir}/dbus_systemcalc.py"

do_install () {
    install -d ${D}${bindir}
    cp -r ${S}/* ${D}${bindir}

    install -d ${D}/${sysconfdir}/dbus-1/system.d
    install -m 644 ${WORKDIR}/com.victronenergy.system.conf ${D}/${sysconfdir}/dbus-1/system.d
}
