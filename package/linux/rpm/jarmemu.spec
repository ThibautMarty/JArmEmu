Name:           jarmemu
Version:        0.1.5
Release:        BETA
Summary:        JArmEmu
Group:          Development/Tools
BuildArch:      noarch

License:        GPL3
Packager:       Alexandre Leconte <aleconte@insa-rennes.fr>
Source0:        %{name}-%{version}.tar.gz

Requires:       bash, desktop-file-utils

%description
The portable arm simulator
JArmEmu is a simple simulator with a graphical interface that offers basic control and information about a simulated ARMv7 architecture.

%prep
%setup -q

%install
rm -rf $RPM_BUILD_ROOT
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/java
mkdir -p $RPM_BUILD_ROOT/%{_datadir}/icons
mkdir -p $RPM_BUILD_ROOT/%{_bindir}

cp -r java/ $RPM_BUILD_ROOT/%{_datadir}/
cp -r icons/ $RPM_BUILD_ROOT/%{_datadir}/
cp -r mime/ $RPM_BUILD_ROOT/%{_datadir}/

install jarmemu $RPM_BUILD_ROOT/%{_bindir}/jarmemu

desktop-file-install --dir=%{buildroot}%{_datadir}/applications/ fr.dwightstudio.jarmemu.gui.JArmEmuApplication.desktop

%post
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

%postun
touch --no-create %{_datadir}/icons/hicolor
if [ -x %{_bindir}/gtk-update-icon-cache ]; then
  %{_bindir}/gtk-update-icon-cache -q %{_datadir}/icons/hicolor;
fi
update-mime-database %{_datadir}/mime &> /dev/null || :
update-desktop-database &> /dev/null || :

%clean
rm -rf $RPM_BUILD_ROOT

%files
%{_datadir}/java/jarmemu/*

%{_datadir}/mime/packages/text-x-jarmemu-source.xml

%{_bindir}/jarmemu

%{_datadir}/icons/hicolor/128x128/apps/jarmemu.png
%{_datadir}/icons/hicolor/16x16/apps/jarmemu.png
%{_datadir}/icons/hicolor/24x24/apps/jarmemu.png
%{_datadir}/icons/hicolor/256x256/apps/jarmemu.png
%{_datadir}/icons/hicolor/32x32/apps/jarmemu.png
%{_datadir}/icons/hicolor/48x48/apps/jarmemu.png
%{_datadir}/icons/hicolor/512x512/apps/jarmemu.png
%{_datadir}/icons/hicolor/64x64/apps/jarmemu.png

%{_datadir}/applications/fr.dwightstudio.jarmemu.gui.JArmEmuApplication.desktop

%changelog
* Wed Nov 8 2023 Alexandre Leconte <aleconte@insa-rennes.fr>
- Creating SPEC File
