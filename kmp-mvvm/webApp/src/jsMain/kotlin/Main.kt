import com.example.kmp_mvvm.model.CountObject
import react.Fragment
import react.create
import react.dom.client.createRoot
import web.dom.ElementId
import web.dom.document

fun main() {
    createRoot(document.getElementById(ElementId("root"))!!)
        .render(Fragment.create {
            CountObject.entries.forEach { entry ->
                Counter {
                    countObject = entry
                }
            }
        })
}
