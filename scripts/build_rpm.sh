#!/bin/bash

# Aller à la racine du dépôt
cd $(git rev-parse --show-toplevel) || exit 1

# Constantes
VER=0.1.2
RELEASE=ALPHA
BF=$HOME/rpmbuild

# Clean
rm -r $BF
rm ./target/jarmemu-$VER-$RELEASE.noarch.rpm

rpmdev-setuptree
TMP=$(mktemp -d -q)
CPF=$TMP/jarmemu-$VER

# Compression
mkdir -p $CPF/java/jarmemu
cp ./package/linux/common/fr.dwightstudio.jarmemu.gui.JArmEmuApplication.desktop $CPF/
cp -r ./package/linux/common/icons $CPF/
cp -r ./package/linux/common/mime $CPF/
cp ./target/JArmEmu.jar $CPF/java/jarmemu/
cp -r ./target/lib/ $CPF/java/jarmemu/

tar -C $TMP/ -zcf $CPF.tar.gz jarmemu-$VER
cp $CPF.tar.gz $BF/SOURCES/
rm -r $TMP

# Build
cp ./package/linux/rpm/jarmemu.spec $BF/SPECS/
rpmbuild -ba $BF/SPECS/jarmemu.spec

# Clean et rendu
cp $BF/RPMS/noarch/jarmemu-$VER-$RELEASE.noarch.rpm ./target/JArmEmu-$VER-$RELEASE.noarch.rpm
rm -r $BF
