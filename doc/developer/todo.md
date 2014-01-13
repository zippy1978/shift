
# Shift - TODO list

<t>✘</t> To do

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

<t>✘</t> 'Refresh' on all items

### Editors pane

<d>✔ 08/16/13</d> Add confirmation dialog on editor closing if file is not saved.

### Status bar


<d>✔ 08/18/13</d> Add cursor position on the current editor (line and column number)

### Builtin plugin

#### Wizards

<d>✔ 12/20/13</d> Finish HTML5 project wizard  

<t>✘</t> Add an [Initializr](http://www.initializr.com/) project wizard

#### HTML Preview

<d>✔ 01/07/14</d> Device presets with name, dimensions, screen density  

<d>✔ 12/29/13</d> Track active document  

#### Remote Preview

<d>✔ 12/12/13</d> Prevent launching more than one instance

<d>✔ 12/13/13</d> Display remote URL

<d>✔ 12/26/13</d> Display list of connected Browser

<d>✔ 12/26/13</d> Mesure rendering time per browser

<d>✔ 12/27/13</d> Ping feature to know which browser it is.

<d>✔ 12/27/13</d> Track active document 


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

<t>✘</t> Welcome window display must display version and build numbers + warning message when snapshot release.

<d>✔ 09/12/13</d> Icon

<t>✘</t> Styling : button, toggles, selects...


## Backlog

### Preferences

<t>✘</t> Preferences window (with tree navigation)

### Output pane

<t>✘</t> Output pane at the bottom of the MainController to display : lint and other output in tabs (or something else...)

### Code editors

<t>✘</t> Javascript completion  

<t>✘</t> CSS completion  

<t>✘</t> XHTML completion  with XSD

<t>✘</t> Lint and marker support

### Menu

<t>✘</t> Search file by name

<t>✘</t> Search file with content

### WOPE support

<t>✘</t> WOPE XHTML editor with completion  

<t>✘</t> WOPE preferences pane (selection of WOPE runtime and licence)  

<t>✘</t> WOPE preview (device presets)

### Plugins

<t>✘</t> External plugin support

<t>✘</t> External plugin packaging (mvn artifact ? gradle ?)

### Project Navigator

<t>✘</t> Drag n drop to move file and folders (from within the same project)
<t>x</t> 'Duplicate' on all items

### Preprocessor

<t>✘</t> Preprocessor mechanism: enhance / modify documents before they are served or built.

<t>✘</t> HTML Template preprocessor implementation : use groovy DSL in HTML comment to include other files.

<t>✘</t> CSS preprocessor to support LESS.

<t>✘</t> CSS preprocessor to support CSS preview (check rendition of elements with a given style - use CSS comment and Groovy DSL).

### Remote Preview

<t>✘</t> Screenshot feature

<t>✘</t> Replace basic table with more visual widget (grid or slider with screenshots)

### HTML Preview

<t>✘</t> Add WebKit console  

### Tools

<t>✘</t> Tool to manage .PSD slicing and multi density resizing.

<t>✘</t> Tool to check appication resources usage.
