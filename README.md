This is the source behind [elliotquotes.com](http://elliotquotes.com). There is very little to it. When you run

`groovy rizzo.groovy`

[the script](rizzo.groovy) will use [Groovy&#8217;s template framework](http://groovy.codehaus.org/Groovy+Templates) to create

* [a home page with the last post](http://elliotquotes.com/), using the [home.html](templates/home.html) template

* [a page for each post](http://elliotquotes.com/silence/), using the [post.html](templates/post.html) template

* [an Archives page](http://elliotquotes.com/archives/), using the [archives.html](templates/archives.html) and [arc_entry.html](templates/arc_entry.html) templates

[Post](posts/silence.html) files are in HTML format. They contain a `title`, a `dateCreated` (used on the Archives page to indicate the date of posting), a `dateWritten`, and the post `content`.
