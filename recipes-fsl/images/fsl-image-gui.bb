# Copyright (C) 2015 Freescale Semiconductor
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Freescale Image to validate i.MX machines. \
This image contains everything used to test i.MX machines including GUI, \
demos and lots of applications. This creates a very large image, not \
suitable for production."
LICENSE = "MIT"

inherit core-image features_check

### WARNING: This image is NOT suitable for production use and is intended
###          to provide a way for users to reproduce the image used during
###          the validation process of i.MX BSP releases.

# Add machine learning for certain SoCs
ML_PKGS                   ?= ""
ML_PKGS_mx8                = "${@bb.utils.contains('DISTRO', 'fslc-xwayland', 'packagegroup-fsl-ml', 'packagegroup-imx-ml', d)}"

IMAGE_FEATURES += " \
    splash \
    package-management \
    ssh-server-dropbear \
    hwcodecs \
    debug-tweaks \
    nfs-server \
    tools-debug \
    tools-testapps \
    ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'weston', \
       bb.utils.contains('DISTRO_FEATURES',     'x11', 'x11-base x11-sato', \
                                                       '', d), d)} \
"

CORE_IMAGE_EXTRA_INSTALL += " \
	packagegroup-core-full-cmdline \
	packagegroup-tools-bluetooth \
	packagegroup-imx-tools-audio \
	packagegroup-fsl-tools-gpu-external \
	packagegroup-fsl-tools-testapps \
	packagegroup-fsl-tools-benchmark \
	packagegroup-fsl-gstreamer1.0 \
	packagegroup-fsl-gstreamer1.0-full \
	packagegroup-imx-isp \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'xterm', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11 wayland', 'weston-xwayland', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'weston-init', \
	   bb.utils.contains('DISTRO_FEATURES',     'x11', 'packagegroup-core-x11-sato-games', \
							 '', d), d)} \
	nodejs \
	screen \
	tcf-agent \
	openssh-sftp-server \
	libgpiod \
	libgpiod-tools \
	${ML_PKGS} \
"

# only for Android enabled machines
IMAGE_INSTALL_append_imxgpu3d = " \
	android-tools \
	android-tools-adbd \
	android-tools-fstools \
"

# only for DRM enabled machines
IMAGE_INSTALL_append_imxdrm = " \
	libdrm-tests \
"

CORE_IMAGE_EXTRA_INSTALL_append_mx8 = "\
    packagegroup-fsl-tools-gpu \
"

systemd_disable_vt () {
    rm ${IMAGE_ROOTFS}${sysconfdir}/systemd/system/getty.target.wants/getty@tty*.service
}

IMAGE_PREPROCESS_COMMAND_append = " ${@ 'systemd_disable_vt;' if bb.utils.contains('DISTRO_FEATURES', 'systemd', True, False, d) and bb.utils.contains('USE_VT', '0', True, False, d) else ''} "
