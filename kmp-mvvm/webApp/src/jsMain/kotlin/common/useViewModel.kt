import react.use.useConstant

fun <VM : Any> useViewModel(create: () -> VM): VM =
    useConstant(create)
