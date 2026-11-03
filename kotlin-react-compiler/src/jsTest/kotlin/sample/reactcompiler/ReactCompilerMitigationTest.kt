package sample.reactcompiler

import react.FC
import react.ReactNode
import react.create
import react.dom.server.renderToStaticMarkup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class ReactCompilerMitigationTest {
    @Test
    fun rewrittenHooksRunAsReactHooks() {
        val selected = mutableListOf<String>()
        val (filtered, onSelectIndex) = renderHook {
            useSearchResults(arrayOf("Kotlin/JS", "React Compiler", "Kotlin Multiplatform"), "kotlin") { selected += it }
        }
        assertEquals(listOf("Kotlin/JS", "Kotlin Multiplatform"), filtered.toList())
        onSelectIndex(1)
        onSelectIndex(99)
        assertEquals(listOf("Kotlin Multiplatform", "Results"), selected)

        assertEquals(listOf("a", "b", "c"), renderHook { useMemoizedList("a", "b", "c") })

        val buildRequest = renderHook { useUserRequestUrl(UserId(42), 1500.milliseconds, 7L) }
        assertEquals("GET /api/users/42?timeout=1500&v=7 (user=42)", buildRequest())
    }

    @Test
    fun loweredComponentsRenderTheBuilderMarkup() {
        val counter = "<div><span>Clicks: 0</span><button>Increment</button></div>"
        assertEquals(counter, render(CounterCard.create { title = "Clicks"; step = 2 }))
        assertEquals(counter, render(InlineFragmentCounter.create { title = "Clicks"; step = 2 }))
        assertEquals("<div><span>Count: 0</span><button>+1</button></div>", render(ZeroArgCounter.create()))
        assertEquals(
            "<div><h3>Sure?</h3><footer><button type=\"button\">Proceed</button></footer></div>",
            render(ConfirmBanner.create { message = "Sure?" }),
        )

        val badge = "<span><strong>Inbox</strong> (3)</span>"
        assertEquals(badge, render(StatusBadge.create { label = "Inbox"; count = 3 }))
        assertEquals(badge, render(AnnotatedStatusBadge.create { label = "Inbox"; count = 3 }))

        assertEquals("", render(ExpandableSection.create { visible = false; title = "T"; subtitle = "S" }))
        assertEquals(
            "<div><header><span>T</span><button>Expand</button></header></div>",
            render(ExpandableSection.create { visible = true; title = "T"; subtitle = "S" }),
        )

        assertEquals(
            "<div><span>Count: 0 (pending: false)</span><button>Increment in Transition</button></div>",
            render(DestructuredStateDemo.create()),
        )
        assertEquals("<div><span>[ACCENT] Item: L</span></div>", render(EffectAndGettersDemo.create { label = "L" }))
        assertEquals(
            "<div><span>T: 0</span><button>Increment</button></div>",
            render(CompilerFriendlyCounter.create { title = "T"; step = 1 }),
        )
    }
}

private fun render(element: ReactNode): String =
    renderToStaticMarkup(element)

private fun <T> renderHook(hook: () -> T): T {
    var result: T? = null
    renderToStaticMarkup(FC { result = hook() }.create())
    return result.unsafeCast<T>()
}
