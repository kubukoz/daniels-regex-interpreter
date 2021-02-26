# Daniel's Regex Interpreter

An example application built from the [ce3.g8 template](https://github.com/kubukoz/ce3.g8).

```
Daniel Spiewak:palm_tree:  14 hours ago
Fun Scala Exercises (the first two work in any language; meant to be done in order)
Implement a regular expressions interpreter (ignore performance). I recommend implementing the following interface:
trait Regular {
  def |(that: Regular): Regular
  def ~(that: Regular): Regular
  def ? : Regular
  def + : Regular
  def apply(str: String): Boolean
}
object Regular {
  def apply(c: Char): Regular = ???
}
2. Ignoring stack-safety, make it purely functional (assuming you didn’t do so in step 1)
3. Adjust the apply method on Regular above to take an fs2 Stream[F, Char] instead of String. (hint: use Pull)
4. Optimize the performance of step 3 so that parsing a Chunk[Char] is just as fast as parsing a String was in step 2
5. Optimize the performance of step 4 so that there are zero allocations within a Chunk and all processing is stack-safe
The first step here is pretty easy. This increases in escalating difficulty. By the end of it, if you do it correctly, you have exercised everything you need to know in order to implement extremely high-performance FP on the JVM, and you've also learned a lot of advanced things about Fs2.
I'll post solutions sometime in the next few weeks when I get some time to sit down and do it. Have fun! (edited)

Oh also as a bonus round for extreme fun… After you finish the final step, revise it to make the whole thing run in O(n) (where n is the length of the string). It’s very likely that your implementation in all previous stages will be O(n^2), which is okay.
```

## Run application

```shell
sbt run
```

## Run tests

```shell
sbt test
```
