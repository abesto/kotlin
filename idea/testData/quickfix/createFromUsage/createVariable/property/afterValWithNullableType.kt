// "Create property 'foo' from usage" "true"
// ERROR: Property must be initialized or be abstract

class A<T>(val n: T) {
    val foo: A<T>?

}

fun test() {
    val a: A<Int>? = A(1).foo
}
