def rubies = ["2.1.6", "2.2.2"]
def platforms = ["osx", "linux-x86", "linux-x86_64"]

rubies.each { ruby ->
  platforms.each { pf ->
    job("ruby-${ruby}-${pf}-build") {
      description("Build for Ruby ${ruby} on ${pf}")

      scm {
	git {
	  remote {
	    github("jdenen/dow")
	  }
	  extensions {
	    cleanBeforeCheckout()
	  }
	}
      }

      wrappers {
	rbenv("${ruby}") {
	  ignoreLocalVersion()
	  gems("bundler", "rake")
	}
      }

      steps {
	shell("bundle check || bundle install")
	shell("bundle exec rake package:${pf}")
        shell("tar -czf dow-${ruby}-${pf}.tar.gz dow-${ruby}-${pf}")
      }
      
      publishers {
        archiveArtifacts {
          pattern("dow-${ruby}-${pf}.tar.gz")
          onlyIfSuccessful()
	}
	slackNotifications {
	  projectChannel("#general")
	  notifyFailure()
	  notifyRepeatedFailure()
	  notifyBackToNormal()
	}
	
      }
    }
  }
}
