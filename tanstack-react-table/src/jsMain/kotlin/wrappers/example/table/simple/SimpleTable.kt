package wrappers.example.table.simple

import emotion.styled.styled
import react.ChildrenBuilder
import react.FC
import react.PropsWithValue
import react.dom.html.ReactHTML.table
import web.cssom.Auto.Companion.auto
import web.cssom.Border
import web.cssom.BorderCollapse
import web.cssom.LineStyle.Companion.solid
import web.cssom.WhiteSpace
import web.cssom.px
import wrappers.example.theme.Theme

internal external interface SimpleTableProps<D : Any> : PropsWithValue<TableInstance<D>>

internal val SimpleTable: FC<SimpleTableProps<*>> = FC { props ->
    SimpleTable(props)
}

private fun <D : Any> ChildrenBuilder.SimpleTable(props: SimpleTableProps<D>) {
    val table = props.value

    TableBase {
        TableHead {
            value = table.headerGroups
        }

        TableBody {
            value = table.rows
        }
    }
}

private val TableBase = table.styled {
    width = 400.px
    borderSpacing = 0.px
    borderCollapse = BorderCollapse.collapse
    whiteSpace = WhiteSpace.nowrap
    border = Border(2.px, solid, Theme.Stroke.Gray)
    margin = auto
}
