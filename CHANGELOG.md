# Changelog

## [Unreleased]

## Added

- Added "Day Offset" option to adjust Hijri date by ±1 day to match local moon sightings or personal observance

## Changed

- Use `AlarmManager` instead of `WorkManager` for accurate date updates

## [v0.11.2] - 2025-03-06

### Changed
- Minor updates and improvements

## [v0.11.1] - 2025-02-28

### Added
- Update widget on click functionality

## [v0.11.0] - 2024-12-11

### Added
- Option to change when the new day starts

### Fixed
- Fixed Arabic names for Jumada months

## [v0.10.0] - 2024-10-25

### Changed
- Improved workers functionality
- Added CI workflow to publish to Google Play Store

## [0.9.0] - 2024-09-30

### Added
- Auto close configuration page when hitting save if opened from widget itself

### Changed
- Changed default cell width to 3 on Android 12 and higher

## [0.8.0] - 2024-06-08

### Added
- Restore to defaults button
- More accurate widget preview instead of an image in widgets picker on recent Android versions
- Open widget settings from launcher (visible when editing widget in launcher)
- New selected and unselected icons in settings
- Color picker shown below instead of a dialog

### Changed
- Automatically change the background color of the preview widget in settings when selected widget text color doesn't contrast well with the background
- Will no longer close the settings app when applying changes
- Will no longer navigate back in settings when choosing widget language or color

### Fixed
- Fixed text overlapping in widget preview when using a large text size

## [0.7] - 2024-06-07

### Added
- Enable or disable widget shadow
- Change widget text size
- Custom widget color

### Changed
- Switched the hijri date source to use Android built-in algorithm
- Reduced widget shadow blur to 1px

## [0.6] - 2024-05-17

### Changed
- New look for settings app

## [0.5] - 2024-05-13

### Added
- Change launcher icon each day with an icon that showcases the current day of the hijri month

## [0.4] - 2024-05-12

### Added
- Support for Dynamic (Material You), system, Dark and Light themes for the widget
- Dynamic themed icon

### Changed
- New updated icon

## [0.3] - 2024-05-07

### Added
- Preview image

### Changed
- Updated icon

### Fixed
- Fixed empty text in widget after first install, which required manual sync of database

## [0.2] - 2024-05-06

### Added
- Support dark theme in settings
- Support Material You in settings
- New app icon

## [0.1] - 2024-05-05

### Added
- Initial Release
