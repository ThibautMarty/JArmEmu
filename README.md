# JArmEmu
### Simple ARMv7 simulator written in Java, intended for educational purpose.

## Features
JArmEmu is a simple, user-friendly simulator that provides basic control and information about a simulated ARMv7 architecture.

JArmEmu is powered by an ARMv7 interpreter built *Ex Nihilo* for this project, which provides real-time syntax highlighting,
intelligent auto-completion, memory, stack and register monitoring...

You can write your program using the ARMv7 instruction set (refer to
[Instructions.md](https://github.com/Dwight-Studio/JArmEmu/blob/main/Instructions.md)) and include GNU Assembly directives (only the basic ones are implemented, you can refer to syntax
highlighting or auto-completion to see if it is available).

# Install

## Fedora
[![Fedora](https://img.shields.io/badge/Fedora-294172?style=for-the-badge&logo=fedora&logoColor=white)](#fedora)
[![Nobara](https://img.shields.io/badge/Nobara-black?style=for-the-badge)](#fedora)

[![Copr build status](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/status_image/last_build.png)](https://copr.fedorainfracloud.org/coprs/dwight-studio/JArmEmu/package/jarmemu/)

JArmEmu is available on Fedora Copr:

```bash
sudo dnf copr enable dwight-studio/JArmEmu
sudo dnf install jarmemu
```

## ArchLinux
[![Arch Linux](https://img.shields.io/badge/Arch_Linux-1793D1?style=for-the-badge&logo=arch-linux&logoColor=white)](#ArchLinux)
[![Arch Linux](https://img.shields.io/badge/manjaro-35BF5C?style=for-the-badge&logo=manjaro&logoColor=white)](#ArchLinux)

[![AUR](https://img.shields.io/aur/votes/jarmemu.svg)](https://aur.archlinux.org/packages/jarmemu)

Use your favorite AUR Helper (`yay` for instance):

```bash
yay -S jarmemu
```

## Windows
[![Windows](https://img.shields.io/badge/Windows-0079D6?style=for-the-badge&logo=windows&logoColor=white)](#Windows)
[![Chocolatey](https://img.shields.io/badge/Chocolatey-000000?style=for-the-badge&logo=chocolatey&logoColor=white)](#Windows)

JArmEmu is available on Chocolatey, but the repository is not actively maintained:

```bash
choco install fr.dwightstudio.jarmemu --pre 
```

You can download an installer for JArmEmu on the [release page](https://github.com/Dwight-Studio/JArmEmu/releases/latest). The executables aren't signed, and can trigger a warning screen from Windows UAC (which you can simply ignore).

## Debian
[![Debian](https://img.shields.io/badge/Debian-A81D33?style=for-the-badge&logo=debian&logoColor=white)](#debian)
[![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)](#debian)
[![Pop! OS](https://img.shields.io/badge/Pop!_OS-48B9C7?style=for-the-badge&logo=Pop!_OS&logoColor=white)](#debian)
[![Linux Mint](https://img.shields.io/badge/Linux_Mint-87CF3E?style=for-the-badge&logo=linux-mint&logoColor=white)](#debian)
[![Kali Linux](https://img.shields.io/badge/Kali_Linux-557C94?style=for-the-badge&logo=kali-linux&logoColor=white)](#debian)

JArmEmu is not yet available on Launchpad (maybe soon!).

Meanwhile, you can download and install the DEB packages from the [release page](https://github.com/Dwight-Studio/JArmEmu/releases/latest).

## Licence
This project was created by Kévin "FoxYinx" TOLLEMER and Alexandre "Deleranax" LECONTE, students at INSA Rennes (independent
project). It is distributed in open source under GPL3 (refer to the LICENCE file).
