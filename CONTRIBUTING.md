# Contributing to Trew

### Pull Requests and Code Style
Pull requests must have a good description, a reason for existing, and an acceptable code
- **Bad:** "Add nice feature"
- **Bad:** "Fix bug"
- **Good:** "Add assisted injections to the core module"
- **Good:** "Fix binding duplication bug in multi-bindings"

Follow **D.R.Y.** and **K.I.S.S.** principles.

**Pull Requests fixing typos or other very simple things aren't merged!, report it in an Issue**
There're some rules for commiting to a pull request:
- Don't made big changes in one commit, separate them into other commits explaining its reason to exist.
- Add documentation!
- If the modification is big, please put it in a separate module, it will be tested and moved to the core later (if necessary)
- Add unit tests, this is important for the continuous integration and know if the application works

### Proposing changes to the application design
Open an issue for this, this must be discussed by other contributors, include the benefits this design change will bring and,
if there is something wrong with the current design, report it too. For example: proposing a new package tree?, give us the benefits and what's wrong with the current package tree.

### Reporting Issues
You can report issues in the [Issues](https://github.com/yusshu/trew/issues/) section, these issues must contain a detailed report.
In case of a bug, include classes that involve the bug, the modules and the class that creates the Injector. Most of the time this is enough, but if you have a rare bug, we will ask you for a few more things **(But never personal data!)**. Try to give us a replicable error. You can use another resources like [Pastebin](https://pastebin.com/) or [Hastebin (1)](https://hastebin.com/), [Hastebin (2)](https://hasteb.in/) **Other code servers will not be supported.**
 
In case of documentation typos or other errors, include the classname (preferably the link on GitHub to this class), include the word or section that is **wrong**

Examples:
> #### [Bug] Module doesn't get installed
> I pass a Module to the `create` static method of `Injector` and it doesn't get installed
> Injector creation:
> ```java
> Injector injector = Injector.create(new MyModule());
> ```
> The module:
> ```java
> public class MyModule implements Module {
>   @Override
>   public void configure(Binder binder) {
>     System.out.println("Called!");
>   }
> }
>```
> And it does nothing...

Thanks for contributing!
