package wrappers.example.table

import react.FC
import wrappers.example.entities.Photo
import wrappers.example.table.selection.SelectionContext
import wrappers.example.table.selection.selection
import wrappers.example.table.simple.SimpleTable
import wrappers.example.table.simple.SimpleTableProps

internal val PhotoTable = FC {
    val table = usePhotoTable()

    SelectionContext(table.meta.selection) {
        SimpleTable<SimpleTableProps<Photo>> {
            value = table
        }
    }
}
