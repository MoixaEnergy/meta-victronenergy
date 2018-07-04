require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

COMPATIBLE_MACHINE = "nanopi"

PROVIDES = ""

SRCREV = "d9a73668c3801c76847fa114774b0b9c60bf6158"
SRC_URI = " \
	gitsm://git@github.com/victronenergy/u-boot.git;protocol=ssh;branch=mans/nanopi-moixa \
	file://fw_env.config \
	"
#SRC_URI[md5sum] = "7698560176f9c6b214fa914a87830ed5"
#SRC_URI[sha256sum] = "53c9fb151757b12144b00bb2221f6ad39c095a507044fdfe027677414f84e3a2"

S = "${WORKDIR}/git"
#S = "${WORKDIR}/u-boot-${PV}"

do_compile () {
	oe_runmake -C ${S} ${UBOOT_MACHINE}
	oe_runmake -C ${S} CC="${CC} ${CFLAGS} ${LDFLAGS}" envtools
}

do_install () {
	install -d ${D}/${base_sbindir}
	install ${S}/tools/env/fw_printenv ${D}/${base_sbindir}
	ln -sf fw_printenv ${D}${base_sbindir}/fw_setenv

	install -d ${D}${libdir}
	install -m 644 ${S}/tools/env/lib.a ${D}${libdir}/libubootenv.a

	install -d ${D}${sysconfdir}
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}
}

do_deploy[noexec] = "1"

FILES_${PN} = "${base_sbindir} ${datadir} ${libdir} ${sysconfdir}"
