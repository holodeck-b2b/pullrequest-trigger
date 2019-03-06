# Holodeck B2B Pull Request trigger extension
This extension allows the _Consumer_ application to control when _Pull Request_s are send by Holodeck B2B
instead of the default fixed intervals. It also allows to specify the _simple selection criteria_ of a "selective" 
Pull Request as specified in [section 5.1 of ebMS V3 part 2 (Advanced Features)](http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/part2/201004/cs01/ebms-v3.0-part2-cs01.html#__RefHeading__435723_822242408).

Currently the trigger mechanism is file based; the back-end application writes a XML document to a specific
directory where it is read by the extension and a corresponding Pull Request is triggered.  
__________________
For more information on using Holodeck B2B visit the website at http://holodeck-b2b.org  
Lead developer: Sander Fieten  
Code hosted at https://github.com/holodeck-b2b/pullrequest-trigger  
Issue tracker https://github.com/holodeck-b2b/pullrequest-trigger/issues

## Installation
### Prerequisites
This extension can be used with Holodeck B2B version 4.1.0 and later.

### Configuration
To enable the trigger mechanism a _worker_ that will watch for _trigger documents_ has to be added to the
Holodeck B2B instance. The worker class is `org.holodeckb2b.ebms3.pulling.trigger.filebased.WatchForTriggerFile`
and it takes one parameter _watchPath_ that should point to the directory where the trigger documents are
placed by the back-end. As the worker will look for any file with "xml" extension it is recommended to use
a specific directory for triggers only.  
Furthermore the regular worker _interval_ parameter can be used to set the interval at which the worker
should check for new triggers.

If you will only be using "trigger based" pulling on this Holodeck B2B instance you should disable the
standard "interval based" pull mechanism by removing or deactivating the _pullConfigWatcher_ worker.

## Usage
Pull Request can only be triggered for existing P-Modes that define how the Pull Request must be
processed.So before triggering a request the P-Mode must have been registered in Holodeck B2B, either
manually by an operator or automatically by the back-end.  

To trigger a Pull Request the back-end application must create a _"Pull Request trigger document"_ in the
directory specified in the _watchPath_ parameter of the watcher described above. These "trigger" documents
are XML documents that MUST have the "xml" extension. They must contain at least the P-Mode that governs
the processing of the Pull Request and can further configure the _MessageId_ of the Pull Request, the MPC
 [sub-channel] and selection criteria to use for pulling. Their structure is defined by XML schema
[http://holodeck-b2b.org/schemas/2018/02/pullrequest/trigger](src/main/resources/pulltrigger.xsd).  
**NOTE:** In the current version only the "simple" selection criteria as described in section 5.1 of the
ebMS V3 Part 2 Specification are supported.

When the watcher detects a trigger document it will check that the P-Mode exists and submit a Pull Request
Signal message unit to the Holodeck B2B Core to trigger the pull process. After processing the file, i.e.
after the Pull Request has been submitted, the extension will be changed to "triggered". When an error
occurs on submit the extension will  be changed to "rejected" and information on the error will be written
to a file with the same name but with extension "err".

## Contributing
We are using the simplified Github workflow to accept modifications which means you should:
* create an issue related to the problem you want to fix or the function you want to add (good for traceability and cross-reference)
* fork the repository
* create a branch (optionally with the reference to the issue in the name)
* write your code, including comments
* commit incrementally with readable and detailed commit messages
* run integration tests to check everything works on runtime
* Update the changelog with a short description of the changes including a reference to the issues fixed
* submit a pull request *against the 'next' branch* of this repository

If your contribution is more than a patch, please contact us beforehand to discuss which branch you can best submit the pull request to.

### Submitting bugs
You can report issues directly on the [project Issue Tracker](https://github.com/holodeck-b2b/pullrequest-trigger/issues).
Please document the steps to reproduce your problem in as much detail as you can (if needed and possible include screenshots).

## Versioning
Version numbering follows the [Semantic versioning](http://semver.org/) approach.

## License
This Holodeck B2B extension is licensed under the General Public License V3 (GPLv3) which is included in the LICENSE file in the root of the project.

## Support
Commercial Holodeck B2B support is provided by Chasquis Consulting. Visit [Chasquis-Consulting.com](http://chasquis-consulting.com/holodeck-b2b-support/) for more information.
