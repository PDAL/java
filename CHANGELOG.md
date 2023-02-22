# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.4.0] - 2021-08-01
## Added
* PDAL 2.4.3 [#62](https://github.com/pdal/java/pull/62) (@pomadchin)

## [2.3.1] - 2021-08-01
## Added
* Fix the release to include MacOS native binaries.

## [2.3.0] - 2021-08-01
### Added
- PDAL 2.3.0 [#57](https://github.com/pdal/java/pull/57) (@pomadchin)

## [2.2.0] - 2020-10-04
### Changed
- Build JNI binaries against PDAL 2.2.0 [#47](https://github.com/PDAL/java/pull/47)

## [2.1.6] - 2020-05-16
### Changed
- Make prebuilt JNI binaries conda compatible [#42](https://github.com/PDAL/java/issues/42)

## [2.1.5] - 2020-05-16
### Changed
- Update filter.stats [#39](https://github.com/PDAL/java/pull/39)
- Build JNI binaries against PDAL 2.1.0 [#43](https://github.com/PDAL/java/issues/43)

## [2.1.5-RC3] - 2020-03-13
### Fixed 
- Fix mesh rasterizer [#38](https://github.com/PDAL/java/pull/38)

## [2.1.5-RC2] - 2020-03-11
### Changed
- Allocate rasterized buffer on heap [#37](https://github.com/PDAL/java/pull/37)

## [2.1.5-RC1] - 2020-03-09
### Added
- Add a native mesh rasterization [#36](https://github.com/PDAL/java/pull/36)

### Changed
- Move Scala syntax into the pipeline.syntax package [#33](https://github.com/PDAL/java/issues/33)

## [2.1.4] - 2020-03-03
### Added
- Add more Scala syntax extensions [#32](https://github.com/PDAL/java/pull/32)

## [2.1.3] - 2020-03-03
### Fixed
- Fix Scala DSL hierarchy [#31](https://github.com/PDAL/java/issues/31)

## [2.1.2] - 2020-03-02
### Added
- Expose PDAL Mesh API [#27](https://github.com/PDAL/java/pull/27)
- [CHANGELOG](/CHANGELOG.md)

### Changed
- **Breaking change** // Rename Scala DSL case classes [#28](https://github.com/PDAL/java/issues/28)
- **Breaking change** // Renamed dispose method to close [#27](https://github.com/PDAL/java/pull/27)
- Update Scala DSL up to PDAL 2.0 [#29](https://github.com/PDAL/java/issues/29)

## [2.0.0] - 2020-01-14
### Changed 
- PDAL 2.0 compatible release.
- Added Scala 2.13 support.

## [1.9.0] - 2019-05-15
### Changed 
- Release process improvements.

## [1.8.6] - 2019-04-26
### Changed 
- Release process improvements.

## [1.8.5] - 2019-04-15
### Changed 
- PDAL JNI Bindings thread safety [#19](https://github.com/PDAL/java/issues/19)

### Fixed
- PythonFilter gives invalid syntax [#9](https://github.com/PDAL/java/issues/9)
- Option in filter.Python seems to be misspelled [#8](https://github.com/PDAL/java/issues/8)

## [1.8.4] - 2019-04-09
### Changed
- Make Exceptions Handling better [#17](https://github.com/PDAL/java/pull/17)

## [1.8.3] - 2019-04-09
### Fixed
- CSV files read issues [#15](https://github.com/PDAL/java/issues/15)

## [1.8.2] - 2019-03-27
### Fixed
- Fix dimName calls to be better, otherwise CSV reads wont work [#14](https://github.com/PDAL/java/pull/14)

## [1.8.1] - 2019-03-25
### Changed
- Update JTS to make it GeoTrellis compatible [#13](https://github.com/PDAL/java/pull/13)

## [1.7.0-RC4] - 2019-01-14
### Changed
- Scala version update up to 2.12.6.
- Update dependencies and base PDAL version.
- Release process improvements.

### Fixed
- Fix matlab reader ReaderType [#10](https://github.com/PDAL/java/pull/10)
- Fix typo in GdalWrite [#11](https://github.com/PDAL/java/pull/11)

## [1.7.0-RC3] - 2018-04-16
### Changed
- Release process improvements.

## [1.7.0-RC2] - 2018-04-15
### Added
- An [examples](https://github.com/PDAL/java/tree/1.7.0-RC2/examples/pdal-jni) project.

### Changed
- Release process improvements.

## [1.7.0-RC1] - 2018-04-15
### Changed
- Moved from the PDAL repo and established own lifecycle.

[Unreleased]: https://github.com/PDAL/java/compare/v2.4.0...HEAD
[2.4.0]: https://github.com/PDAL/java/compare/v2.3.1...v2.4.0
[2.3.1]: https://github.com/PDAL/java/compare/v2.3.0...v2.3.1
[2.3.0]: https://github.com/PDAL/java/compare/v2.2.0...v2.3.0
[2.2.0]: https://github.com/PDAL/java/compare/v2.1.6...v2.2.0
[2.1.6]: https://github.com/PDAL/java/compare/v2.1.5...v2.1.6
[2.1.5]: https://github.com/PDAL/java/compare/v2.1.5-RC3...v2.1.5
[2.1.5-RC3]: https://github.com/PDAL/java/compare/v2.1.5-RC2...v2.1.5-RC3
[2.1.5-RC2]: https://github.com/PDAL/java/compare/v2.1.5-RC1...v2.1.5-RC2
[2.1.5-RC1]: https://github.com/PDAL/java/compare/v2.1.4...v2.1.5-RC1
[2.1.4]: https://github.com/PDAL/java/compare/v2.1.3...v2.1.4
[2.1.3]: https://github.com/PDAL/java/compare/v2.1.2...v2.1.3
[2.1.2]: https://github.com/PDAL/java/compare/v2.0.0...v2.1.2
[2.0.0]: https://github.com/PDAL/java/compare/v1.9.0...v2.0.0
[1.9.0]: https://github.com/PDAL/java/compare/v1.8.6...v1.9.0
[1.8.6]: https://github.com/PDAL/java/compare/v1.8.5...v1.8.6
[1.8.5]: https://github.com/PDAL/java/compare/v1.8.4...v1.8.5
[1.8.4]: https://github.com/PDAL/java/compare/v1.8.3...v1.8.4
[1.8.3]: https://github.com/PDAL/java/compare/v1.8.2...v1.8.3
[1.8.2]: https://github.com/PDAL/java/compare/v1.8.1...v1.8.2
[1.8.1]: https://github.com/PDAL/java/compare/v1.7.0-RC4...v1.8.1
[1.7.0-RC4]: https://github.com/PDAL/java/compare/v1.7.0-RC3...v1.7.0-RC4
[1.7.0-RC3]: https://github.com/PDAL/java/compare/v1.7.0-RC2...v1.7.0-RC3
[1.7.0-RC2]: https://github.com/PDAL/java/compare/v1.7.0-RC1...v1.7.0-RC2
[1.7.0-RC1]: https://github.com/PDAL/java/compare/v1.7.0-RC1...v1.7.0-RC1
