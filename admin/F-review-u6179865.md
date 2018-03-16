Reviewer: Yifan Cheng (u6179865)
Component: <Board.java 97-150>
Author:  Jiawen He (u6227591)

Review Comments :

Advantages :

1. Board.java: 100-103, 114-117, 126-130
   The code is well commented. Parameters are well stated, making the structure very clear.

2. Board.java: 97-150
   The idea of construct a `FXPiece` class that extends `ImageView` is quite smart. And this make the rest of Javafx work easier to go.
   ( Able to use predefined constructors and methods. ) The code is well-structured because we only have to instantiate non-static fields and methods;
   For instance, many methods of the superclass (ImageView) such as setLayout, setScale ...... are used instead of
   complicatedly and unnecessarily writing redundant codes.

3. Board.java: 108, 110, 111, 139, 140, 142, 144
   The author created some static variables on top of the whole class, so that they can be easily reused and edited
   without redundant declaration or instantiation.

4. Board.java: 97 - 150
   The  and methods names are proper using camel rule, and the style is consistent throughout;

5. Board.java: 97-150
   This class contains three constructors, with each adding one char into it. This is very cunning because
   the different instances of the constructor enable it to read different amount of given information
   ( e.g. with orientation or not; with position or not);

6. Board.java : 106, 134
   Throwing exception makes a check of the validity of the input parameter, which makes the program safer and easier to
   evaluate and debug if there is something wrong with previous methods.




Down sides :

7. Board.java: 118-124
   The author would better to add an exception throw in the second constructor. When the second
  `orientation` char is not in 'A' - 'H', this shouldn't work and should throw an exception.

8. Board.java: 144
   The code layout in y direction is not readable enough. Readers might not get how the `homeY` is reached.

9. Board.java: 136
   The last condition in `if` statement `position <= 'y'` is always true, so it can be eliminated to make the code more concise;
