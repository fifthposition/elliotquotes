import java.text.SimpleDateFormat
import groovy.text.SimpleTemplateEngine

//Define all the things
def root = new File("/Users/ebenezer/Works/elliotquotes/")
def published = new File("$root/published/")
published.mkdirs()
def homeTemplate = new File("$root/templates/home.html")
def postTemplate = new File("$root/templates/post.html")
def archivesTemplate = new File("$root/templates/archives.html")
def archivesEntryTemplate = new File("$root/templates/arc_entry.html")
def posts = []
def engine = new SimpleTemplateEngine()
SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy")

//lastUpdated is not used yet; nonetheless
class Post {
    String title
    String content
    String name
    Date dateWritten
    Date dateCreated
    Date lastUpdated
}

//Read post files
new File("$root/posts/").eachFile { file ->
    List postText = file.readLines()
    def name = file.name[0 .. file.name.lastIndexOf('.')-1]
    def postDate = formatter.parse(postText[1].toString())
    def dateWritten = formatter.parse(postText[2].toString())
    posts << new Post(title:postText[0], name:name,
                      content: postText[3..-1].join("\n"),
                      dateWritten: dateWritten, dateCreated: postDate,
                      lastUpdated: postDate)
}

//Sort posts and get the most recent one
posts = posts.sort { one, two ->
    one.dateCreated <=> two.dateCreated
}.reverse()
def recent = posts.first()

//Create the home page

def home = ["postTitle": recent.title,
            "postName": recent.name,
            "postDateWritten": formatter.format(recent.dateWritten),
            "postContent": recent.content]
            
String homepage = "${engine.createTemplate(homeTemplate).make(home)}"
new File("$published/index.html").write(homepage)

//Create each post page
posts.each { post ->
    def quote = ["postTitle": post.title,
                 "postDateWritten": formatter.format(post.dateWritten),
                 "postContent": post.content]
    new File("$published/${post.name}/").mkdirs()
    String quotePage = "${engine.createTemplate(postTemplate).make(quote)}"
    new File("$published/${post.name}/index.html").write(quotePage)
}

//Create the archives page
new File("$published/archives/").mkdirs()
def archiveContent = ""
posts.each { post ->
    def quote = ["postDateCreated": formatter.format(post.dateCreated),
                 "postName": post.name,
                 "postTitle": post.title]
    String quoteEntry = "${engine.createTemplate(archivesEntryTemplate).make(quote)}"

    archiveContent += quoteEntry
}
def quoteEntries = ["content": archiveContent]
String entries = "${engine.createTemplate(archivesTemplate).make(quoteEntries)}"
new File("$published/archives/index.html").write(entries)

//Copy the CSS files
new File("$published/css/").mkdirs()
new AntBuilder().copy(toDir: "$published/css/") {
    fileset(dir: "css")
}
