SECTION = "kernel"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

RDEPENDS_kernel-base += "kernel-devicetree rtl8723bu"

KERNEL_CONFIG_COMMAND = "make -C ${S} O=${B} ARCH=arm bbb_defconfig"

KERNEL_DEVICETREE = " \
    am335x-boneblack.dtb \
    bbb-venus.dtb \
    bbb-octo-venus.dtb \
    bbe-venus.dtb \
"

LINUX_VERSION = "4.9"
LINUX_VERSION_EXTENSION = "-venus"

inherit kernel

S = "${WORKDIR}/linux-${PV}"
B = "${WORKDIR}/build"

SRC_URI = "https://github.com/victronenergy/linux/archive/v${PV}.tar.gz"
<<<<<<< HEAD:meta-bsp/recipes-kernel/linux/linux-bbb_4.9.113-bbb5.bb

SRC_URI[md5sum] = "4fee6d42d5df885d391324d5e40a0da1"
SRC_URI[sha256sum] = "5a54a88e3792ab7959d83f4ce6b20e4ab155ef9d81d67050533d833b66ee790c"
=======
SRC_URI[md5sum] = "75893e1e19aa6c5b67c9cd35cde5c114"
SRC_URI[sha256sum] = "6bb8c311eb97aa76c8daed2ca35314e21d3ffeb7d912ec539c1f84e7c41324cf"
>>>>>>> 9151c8f7b43bc28952cd46ba4358e61bc2bc7a63:meta-bsp/recipes-kernel/linux/linux-bbb_4.9.113-bbb11.bb
