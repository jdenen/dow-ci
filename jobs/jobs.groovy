def rubies = ["2.1.6", "2.2.2"]
def platforms = ["osx", "linux-x86", "linux-x86_64"]

rubies.each { ruby ->
  job("ruby-${ruby}-unit-test") {
    description("Unit test against Ruby version ${ruby}")

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

    triggers {
      scm("H/5 * * * *")
    }

    wrappers {
      rbenv("${ruby}") {
	ignoreLocalVersion()
	gems("bundler", "rake")
      }
    }

    steps {
      shell("bundle check || bundle install")
      shell("bundle exec rspec -f RspecJunitFormatter -o result/ruby-${ruby}-unit-test.xml -f progress")
    }

    publishers {
      archiveJunit("result/*.xml")
      slackNotifications {
	projectChannel("#general")
	teamDomain("johnson-ci")
	notifyFailure()
	notifyRepeatedFailure()
	notifyBackToNormal()
      }
    }
    
    platforms.each { pf ->
      publishers {
        downstream("ruby-${ruby}-${pf}-build", "SUCCESS")
      }
    }  
  }

  job("ruby-${ruby}-osx-smoke-test") {
    description("Smoke test the build artifact on OSX")

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

    triggers {
      upstream("ruby-${ruby}-osx-build", "SUCCESS")
    }

    wrappers {
      rbenv("${ruby}") {
	ignoreLocalVersion()
	gems("bundler", "rake")
      }
    }

    steps {
      copyArtifacts("ruby-${ruby}-osx-build") {
	includePatterns("*.gz")
	buildSelector {
	  latestSuccessful(true)
	}
      }
      shell("mkdir spec/support")
      shell("tar -xzf dow-${ruby}-osx.tar.gz")
      shell("mv dow-${ruby}-osx/* spec/support")
      shell("bundle check || bundle install")
      shell("TEST_ARTIFACT=1 bundle exec rspec -f RspecJunitFormatter -o result/dow-${ruby}-osx-smoke-test.xml -f progress")
    }
    
    publishers {
      archiveJunit("result/*.xml")
      slackNotifications {
	projectChannel("#general")
	teamDomain("johnson-ci")
	notifyFailure()
	notifyRepeatedFailure()
	notifyBackToNormal()
      }
    }
  }

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

      steps {
        shell("curl -L -O --fail http://d6r77u77i8pq3.cloudfront.net/releases/traveling-ruby-20150715-${ruby}-${pf}.tar.gz")
        shell("mkdir -p dow-${ruby}-${pf}/lib/ruby")
        shell("tar -xzf traveling-ruby-20150715-${ruby}-${pf}.tar.gz -C dow-${ruby}-${pf}/lib/ruby")
        shell("mv lib/* dow-${ruby}-${pf}/lib")
        shell("cp wrapper.sh dow-${ruby}-${pf}/dow")
        shell("tar -czf dow-${ruby}-${pf}.tar.gz dow-${ruby}-${pf}")
      }
      
      publishers {
        archiveArtifacts {
          pattern("dow-${ruby}-${pf}.tar.gz")
          onlyIfSuccessful
	}
	slackNotifications {
	  projectChannel("#general")
	  teamDomain("johnson-ci")
	  notifyFailure()
	  notifyRepeatedFailure()
	  notifyBackToNormal()
	}
	
      }
    }
  }
}
