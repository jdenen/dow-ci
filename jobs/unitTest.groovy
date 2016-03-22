def rubies = ["2.1.6", "2.2.2"]
def platforms = ["osx", "linux-x86", "linux-x86_64"]

rubies.each { ruby ->
  job("ruby-${ruby}-unit-test") {
    description("Unit test on Ruby version ${ruby}")

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
}
