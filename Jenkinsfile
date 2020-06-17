#!groovy
build('fraudbusters-management', 'java-maven') {
    checkoutRepo()
    loadBuildUtils()

    def javaServicePipeline
    runStage('load JavaService pipeline') {
        javaServicePipeline = load("build_utils/jenkins_lib/pipeJavaService.groovy")
    }

    def serviceName = env.REPO_NAME
    def mvnArgs = '-DjvmArgs="-Xmx256m"'
    def useJava11 = true
    def registry = 'dr2.rbkmoney.com'
    def registryCredsId = 'jenkins_harbor'

    javaServicePipeline(serviceName, useJava11, mvnArgs, registry, registryCredsId)
}