require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

COMPATIBLE_MACHINE = "nanopi"

DEPENDS += "bc-native coreutils-native dtc-native swig-native u-boot-mkimage-native"

SRCREV = "d9a73668c3801c76847fa114774b0b9c60bf6158"
SRC_URI = " \
	gitsm://git@github.com/victronenergy/u-boot.git;protocol=ssh;branch=mans/nanopi-moixa \
	file://install.cmds \
"
#SRC_URI[md5sum] = "7698560176f9c6b214fa914a87830ed5"
#SRC_URI[sha256sum] = "53c9fb151757b12144b00bb2221f6ad39c095a507044fdfe027677414f84e3a2"

S = "${WORKDIR}/git"
#S = "${WORKDIR}/u-boot-${PV}"

do_compile_append () {
	mkimage -A arm -T script -C none -n 'Install Script' \
		-d ${WORKDIR}/install.cmds ${WORKDIR}/install.scr
}

do_deploy_append () {
	install -d ${DEPLOYDIR}
	install -m 0644 ${WORKDIR}/install.scr ${DEPLOYDIR}/install-${MACHINE}.scr
}
