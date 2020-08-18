DESCRIPTION = "Button handler"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit ve_package
inherit daemontools
inherit python-compile

RDEPENDS_${PN} = "\
    python3-core \
    python3-dbus \
    python3-evdev \
    python3-pygobject \
"

SRC_URI = "\
    gitsm://github.com/victronenergy/${BPN}.git;protocol=https;branch=p3 \
"
SRCREV = "b9e81dd2228425fd2279e3d4e47d9dfddd1bd87e"
S = "${WORKDIR}/git"

DAEMONTOOLS_SERVICE_DIR = "${bindir}/service"
DAEMONTOOLS_RUN = "${bindir}/${PN} -D"

do_install () {
    oe_runmake DESTDIR=${D} install
}

do_configure[noexec] = "1"
do_compile[noexec] = "1"
