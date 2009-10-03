def ant = new AntBuilder()   // create an antbuilder
ant.exec(outputproperty:"cmdOut",
             errorproperty: "cmdErr",
             resultproperty:"cmdExit",
             failonerror: "true",
             executable: "java -jar D:\\Dev\\platypus\\platypus\\target\\jar\\Platypus.jar") {      }
println "return code:  ${ant.project.properties.cmdExit}"
println "stderr:         ${ant.project.properties.cmdErr}"
println "stdout:        ${ ant.project.properties.cmdOut}"
return
//def String doThis = "java -jar D:\\Dev\\platypus\\platypus\\target\\jar\\Platypus.jar  D:\\Dev\\platypus\\testdata\\commandonly.platy d:\\dev\\xyz.pdf"
def String doThis = "java -jar D:\\Dev\\platypus\\platypus\\target\\jar\\Platypus.jar"
println( doThis )
def proc = doThis.execute()
proc.waitFor()
println "stderr: ${proc.err.text}"
//def err = proc.err.toString()
//println( err )
