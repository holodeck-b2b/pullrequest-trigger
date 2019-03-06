# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## 1.1.0
###### 2019-01-04
### Added
* Support for setting of the Pull Request's `eb:MessageId` in the trigger.

## 1.0.0
###### (Unreleased)
### Added
* Support for triggering a _Pull Request_ by the _Consumer_ system. The extension
  allows the back-end to trigger both a "normal" Pull Request as described in the
  ebMS3 Core Specification or a "selective" Pull Request as specified in section
  5.1 of ebMS3 Part 2 Advanced Features.  
  NOTE: This version only allows the "simple" selection criteria.
