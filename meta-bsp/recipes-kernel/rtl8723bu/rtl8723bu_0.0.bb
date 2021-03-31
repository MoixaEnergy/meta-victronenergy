SUMMARY = "Add the rtl8723bu driver from SonelSA as an out-of-tree module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = " \
    git://github.com/SonelSA/rtl8723bu_realtek.git;branch=v5.2.17.1;protocol=https;rev=16f71c29b62f7d00b74a8c5865e059abdb82b949 \
    file://0001-fix-makefile-for-openembedded.patch \
    file://0001-Enable-concurrent-mode.patch \
    file://0001-Don-t-print-info-logs.patch \
    file://0002-fix-makefile-disable-powersaving.patch \
    file://0001-disable-roaming.patch \
"

S = "${WORKDIR}/git"

inherit module

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

do_install() {
    # Module
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless
    install -m 0644 8723bu.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/8723bu.ko
}
