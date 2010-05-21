# Loads Pick List Definition content, provided in LexGrid canonical xml format.
#
# Options:
#   -in,--input <uri> URI or path specifying location of the source file.
#   -v, --validate <int> Perform validation of the candidate
#         resource without loading data.  
#         Supported levels of validation include:
#         0 = Verify document is well-formed
#         1 = Verify document is valid
# Example: LoadPickListDefinition -in "file:///path/to/file.xml"
#
java -Xmx1500m -cp "../runtime/lbPatch.jar:../runtime/lbRuntime.jar" org.lexgrid.valuesets.admin.LoadPickListDefinition $@
