def prep_common(def build_env){
  println("will deploy to ${build_env}")
  def name = sh (script: 'git whatchanged -n 1 --pretty=format: --name-only',returnStdout:true).trim().split('/')
  for (i = 0; i < name.length; i++){
     println(name[i])
   if(name[i] == 'ui-web'){
      currentBuild.displayName = name[i]+currentBuild.displayName
      def pipeui=load 'Pipelines/ui-web.groovy'
      pipeui.setup_env()
      pipeui.prep(common_jenkins, common_scm, package_path, package_contents)
      pipeui.unit_tests(package_path)
  } else if(name[i] == 'lambdas') {
      currentBuild.displayName = name[i]+currentBuild.displayName
      def pipeserv=load 'Pipelines/lambdas.groovy'
      pipeui.setup_env(package_path)
      pipeui.prep()
      pipeui.build()
      if($build_env == 'ALL'){
         pipeui.deploy("stg")
        pipeui.unit_tests()
         pipeui.deploy("dev")
        pipeui.unit_tests()
         pipeui.deploy("prod")
      } else {
        pipeui.deploy("${build_env}")
      }
     
      pipeui.api_tests()
      pipeui.post_install()
 }
}
return this



