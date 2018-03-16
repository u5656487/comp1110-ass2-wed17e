Reviewer: Wei XING (u5656487)   
</br>
Component: **StepGame.java _Line 387-420_**  
</br>
Author: Yafan Cheng (u6179865)

1. **StepsGame.java 415:** Use '|' operator to deal with grid and pieces, which is a cunning way to prevent complicated 
data structure operation.
2. **StepsGame.java 410 - 417:** Use back traking method to solve the solutions instead of enumeration, which highly 
improve the speed of getting solutions.
3. **StepsGame.java 387, 399, 410:** Use camel principle to name methods and let the method names well describe the function of mothods.
4. **StepsGame.java 387 - 417 :** Precise and easy to read. Objects that ae only used once are declared where they are used, 
preventing extra declaration which makes the codes long and complicated. Also, expression are concise such as '(char)((i + 'A') + i/25*7)', 'if (canPut(grid, newPiece)) allPoss.add(newPiece);',
   'canPut(grid, newPiece)'.
5. Similar objects such like 'Set<String> allPoss = new HashSet<>();' are declared 3 times, which seems to be  a little bit redundant. However, an non-static
   HashSet<>() can be declared as a field outside each method. When using these Objects, we can simply instantiate them to be different instance fields
   of the original HashSet<>().
6. **StepsGame.java 401 - 405 :** For loop with only one statement does not need actually and can be written in a single 
line to make the code more precise.
7. **StepsGame.java 391 - 392, 403 - 404 :** The for loop can be simplified. For example, an array containing 8 chars ('A' - 'H') 
can be declared statically as well and can be used many times since there are a lot of situations where we should use it.
    