# terkel

A task/event framework for the FTC SDK.

This software was presented in a workshop at the 2017 Los Angeles region FTC kickoff event.

To use this software clone cmacfarl/ftc_app and cmacfarl/terkel into the same directory.  Then, in ftc_app checkout the master-terkel-example branch and open ftc_app in Android Studio.
There's a bit of gradle magic in that branch that will pull in sources from the 
terkel directory if it lives beside ftc_app.

Example
```
cmacfarl@CMACFARL MINGW64 /c/FIRST/tmp
$ git clone https://github.com/cmacfarl/ftc_app.git
Cloning into 'ftc_app'...
remote: Counting objects: 3585, done.
remote: Total 3585 (delta 0), reused 0 (delta 0), pack-reused 3584
Receiving objects: 100% (3585/3585), 609.78 MiB | 7.37 MiB/s, done.
Resolving deltas: 100% (1698/1698), done.
Checking out files: 100% (406/406), done.

cmacfarl@CMACFARL MINGW64 /c/FIRST/tmp
$ git clone https://github.com/cmacfarl/terkel.git
Cloning into 'terkel'...
remote: Counting objects: 4423, done.
remote: Compressing objects: 100% (17/17), done.
remote: Total 4423 (delta 10), reused 15 (delta 5), pack-reused 4398
Receiving objects: 100% (4423/4423), 27.15 MiB | 6.30 MiB/s, done.
Resolving deltas: 100% (2450/2450), done.

cmacfarl@CMACFARL MINGW64 /c/FIRST/tmp
$ cd ftc_app

cmacfarl@CMACFARL MINGW64 /c/FIRST/tmp/ftc_app (master)
$ git checkout master-terkel-example
Switched to a new branch 'master-terkel-example'
Branch master-terkel-example set up to track remote branch master-terkel-example from origin.

cmacfarl@CMACFARL-M6P1H MINGW64 /c/FIRST/tmp/ftc_app (master-terkel-example)
```

Disclaimer:  This is largely student software and it's constantly in flux over the course of the 
season.  Use at your own discretion.  Efforts will be made to keep this software sane, but things 
may be broken now, or may break in the future.  There are still artifacts here that are the result
of early limitations of the SDK that have since been fixed.  YMMV.  

Contributions: If you do something with this software that you think would be useful for the
broader community, we will welcome contributions.  However they must follow these rules

1.  Style must follow the existing style you see here, which is effectively K&R.  Yes I know, K&R is similar but it's not
strictly Sun's Java style.  Braces that begin methods go on a separate line is an example of one deviation 
from Sun.  Contributions that don't follow K&R style at all will not be considered.  Contributions that attempt to follow K&R 
but have a few mistakes but otherwise are useful to the broader community may be asked to fix via review comments.

2. Contributions that are not broadly applicable to all teams will not be considered.

3. Contributions must be submitted with narrative text that describes what is being done and why it's broadly useful. 
Remember that when you submit a pull request you are asking someone else to do some work for you.  Make it as easy
as possible for that person to complete the work you are asking of her.

4. Bug fixes should have clear and reproducible steps to replicate the bug.  The more detail the better.
If it's not entirely obvious at a glance that it's a bug and/or we can't reproduce it probably will not be merged.

Good luck to all and have a great season!

