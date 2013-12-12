
# Shift - TODO list

<t>✘</t> To do

<c>R</c> Reported to next release

<d>✔</d> Completed

## Release 0.1

The purpose of this release is to provide a first stable and usable (but yet simple) code editor with working HTML preview.


### Custom controls

<d>✔ 07/23/13</d> TextField with validation support (blank and regex 'match')

### Project navigator

#### Context menu  

<d>✔ 07/24/13</d> 'New folder' on projects and folders  

<d>✔ 07/26/13</d> Add confirm dialog when unsaved files before closing project  

<d>✔ 09/04/13</d> 'Delete' on all items  

<t>✘</t> 'Rename' on all items  

<t>✘</t> Drag n drop to move file and folders (from within the same project)  

<t>✘</t> 'Refresh' on all items

### Editors pane

<d>✔ 08/16/13</d> Add confirmation dialog on editor closing if file is not saved.

### Status bar


<d>✔ 08/18/13</d> Add cursor position on the current editor (line and column number)

### Builtin plugin

#### Wizards

<t>✘</t> Finish HTML5 project wizard  

<t>✘</t> Add an [Initializr](http://www.initializr.com/) project wizard

#### HTML Preview

<t>✘</t> Device presets with name, dimensions, screen density  

<t>✘</t> Add WebKit console  

<t>✘</t> Track active document  

<t>✘</t> Animated loader  

<t>✘</t> Reset button (reload document and reset navigation)  

#### Remote Preview

<d>✔ 12/12/13</d> Prevent launching more than one instance

<t>✘</t> Display remote URL

<t>✘</t> Display list of connected Browser

<t>✘</t> Mesure rendering time per browser


### Dialogs

<d>✔ 07/26/13</d> Error message dialog  

<d>✔ 07/26/13</d> Confirm (yes / no) dialog  

<d>✔ 12/12/13</d> Picker dialog : pick a value in a choice list (for preview selection, when multiple choices)

### Menu

<d>✔ 08/16/13</d> 'Undo' in 'Edit' menu  

<d>✔ 08/16/13</d> 'Redo' in 'Edit' menu  

<d>✔ 09/12/13</d> 'Copy' in 'Edit' menu  

<d>✔ 09/12/13</d> 'Cut' in 'Edit' menu  

<d>✔ 09/12/13</d> 'Paste' in 'Edit' menu  

<d>✔ 09/12/13</d> 'Select All' in 'Edit' menu  

<t>✘</t> 'Search' in 'Edit' menu  

### Packaging

<t>✘</t> About window  

<d>✔ 09/12/13</d> Icon

## Release 0.2

Better code editing with advanced features and WOPE tight integration.

### Preferences

<t>✘</t> Preferences window (with tree navigation)

### Code editors

<t>✘</t> Javascript completion  

<t>✘</t> CSS completion  

<t>✘</t> XHTML completion  with XSD

### WOPE support

<t>✘</t> WOPE XHTML editor with completion  

<t>✘</t> WOPE preferences pane (selection of WOPE runtime and licence)  

<t>✘</t> WOPE preview (device presets)

### Plugins

<t>✘</t> External plugin support

<t>✘</t> External plugin packaging (mvn artifact ? gradle ?)

## Backlog

### Preoprocessor

<t>✘</t> Preprocessor mechanism: enhance / modify documents before they are served or built.

<t>✘</t> HTML Template preprocessor implementation : use groovy DSL in HTML comment to include other files.

<t>✘</t> CSS preprocessor to support LESS.

<t>✘</t> CSS preprocessor to support CSS preview (check rendition of elements with a given style - use CSS comment and Groovy DSL).

### Tools

<t>✘</t> Tool to maange .PSD slicing and multi density resizing.
