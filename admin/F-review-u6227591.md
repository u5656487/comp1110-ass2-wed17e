Reviewer: Wei Xing (u5656487)
Component: <Board> line 97-150 `FXPiece` class
Author: Jiawen He (u6227591)

Review Comments:

1. StepsGame.java: 300 - 335 He chooses hashmap because hashmap does not allow duplicate elements and this property can
    automatic remove the same solutions in all possible solutions. I think this is a smart choice. And he is quite skilled
    at choosing and using collections.

2. StepsGame.java: 300, 304, 333 The print-out part of the code makes it clear to see and convenient to check the progress
    when we are solving more than one games at a time.

3. StepsGame.java: 300 - 335 The algorithm he used for filtering duplicate is to sort them by the first char of each piece
    first. This is an efficient algorithm.

3. StepsGame.java: 300 - 335 The variable names in the class are not very clear. This makes it hard for readers to catch on.
    It's better to name them according to their utility and content.

4. StepsGame.java: 319 - 324 He used String concatenation in a loop and this takes too much space. It's better to use a
    String builder here. (fixed now)
