import java.text.SimpleDateFormat
import groovy.text.SimpleTemplateEngine

//yay definitions!
def root = new File("/Users/ebenezer/Works/elliotquotes/")
def published = new File("$root/published/")
published.mkdirs()
def homeTemplate = new File("$root/templates/home.html")
def postTemplate = new File("$root/templates/post.html")
def archivesTemplate = new File("$root/templates/archives.html")
SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy h:mm a")
SimpleDateFormat archiveFormatter = new SimpleDateFormat("MMMMM d, yyyy")

class Post {
    String title
    String content
    String name
    Date dateWritten
    Date dateCreated
    Date lastUpdated
}

def posts = []
def engine = new SimpleTemplateEngine()

new File("$root/posts/").eachFile { file ->
    List postText = file.readLines()
    def name = file.name[0 .. file.name.lastIndexOf('.')-1]
    def postDate = formatter.parse(postText[1].toString())
    def dateWritten = archiveFormatter.parse(postText[2].toString())
    posts << new Post(title:postText[0], name:name, content: postText[3..-1].join("\n"), dateWritten: dateWritten, dateCreated: postDate, lastUpdated: postDate)
}

println "and the posts are $posts"

posts.sort { it.dateCreated }.reverse()

new File("$published/index.html").write("${engine.createTemplate(homeTemplate).make(["postTitle": posts.last().title, "postName": posts.last().name, "postDateWritten": archiveFormatter.format(posts.last().dateWritten), "postContent": posts.last().content])}")

posts.each { post ->
    new File("$published/${post.name}/").mkdirs()
    new File("$published/${post.name}/index.html").write("${engine.createTemplate(postTemplate).make(["postTitle": post.title, "postDateWritten": archiveFormatter.format(post.dateWritten), "postContent": post.content])}")
}

new File("$published/archives/").mkdirs()
def archiveContent = ""

posts.sort { it.dateCreated }.reverse().each { post ->
    archiveContent += """
                <tr>
                    <td valign="top" class="date">${archiveFormatter.format(post.dateCreated)}</td>
                    <td valign="top"><a href="#"><a href="/${post.name}/">${post.title}</a></td>
                </tr>
    """
}

new File("$published/archives/index.html").write("${engine.createTemplate(archivesTemplate).make(["content": archiveContent])}")

new File("$published/css/").mkdirs()
new AntBuilder().copy(toDir: "$published/css/") {
    fileset(dir: "css")
}

println "All done!"
