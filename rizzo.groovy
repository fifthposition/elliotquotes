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
SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy h:mm a")
SimpleDateFormat archiveFormatter = new SimpleDateFormat("MMMMM d, yyyy")

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
    def dateWritten = archiveFormatter.parse(postText[2].toString())
    posts << new Post(title:postText[0], name:name, content: postText[3..-1].join("\n"), dateWritten: dateWritten, dateCreated: postDate, lastUpdated: postDate)
}

//Create the home page
new File("$published/index.html").write("${engine.createTemplate(homeTemplate).make(["postTitle": posts.last().title, "postName": posts.last().name, "postDateWritten": archiveFormatter.format(posts.last().dateWritten), "postContent": posts.last().content])}")

//Create each post page
posts.sort { it.dateCreated }.reverse().each { post ->
    new File("$published/${post.name}/").mkdirs()
    new File("$published/${post.name}/index.html").write("${engine.createTemplate(postTemplate).make(["postTitle": post.title, "postDateWritten": archiveFormatter.format(post.dateWritten), "postContent": post.content])}")
}

//Create the archives page
new File("$published/archives/").mkdirs()
def archiveContent = ""
posts.sort { it.dateCreated }.reverse().each { post ->
    archiveContent += "${engine.createTemplate(archivesEntryTemplate).make(["postDateCreated": archiveFormatter.format(post.dateCreated), "postName": post.name, "postTitle": post.title])}"
}
new File("$published/archives/index.html").write("${engine.createTemplate(archivesTemplate).make(["content": archiveContent])}")

//Copy the CSS files
new File("$published/css/").mkdirs()
new AntBuilder().copy(toDir: "$published/css/") {
    fileset(dir: "css")
}
