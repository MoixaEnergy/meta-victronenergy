SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

inherit kernel

COMPATIBLE_MACHINE = "sunxi"

RDEPENDS_kernel-base += "kernel-devicetree"

KERNEL_CONFIG_COMMAND = "oe_runmake -C ${S} O=${B} sunxi_victron_defconfig"

SRCREV="7ab40a0487876fab112f46f9b1173b4f0c3b4114"
SRC_URI = "git://git@github.com/MoixaEnergy/linux.git;protocol=ssh;branch=moixa-hub-tft"

#SRC_URI[md5sum] = "7326d0571ae2e564142068a7e143acf8"
#SRC_URI[sha256sum] = "ce2ab69783381a2ba87598f096047a66c06ee169054c384762316c296f40fea5"

S = "${WORKDIR}/git"

# fix make[3]: *** [scripts/extract-cert] Error 1
DEPENDS += "openssl-native"
HOST_EXTRACFLAGS += "-I${STAGING_INCDIR_NATIVE}"
