require 'formula'

class WhiskCli < Formula
    homepage 'https://github.com/whisk-co-uk/whisk-api-client/wiki'
    url 'https://github.com/downloads/whisk-co-uk/whisk-api-client/whisk-cli-0.1.jar'

    def startup_script name
        <<-EOS.undent
            #!/bin/bash
            java -jar "#{libexec}/whisk-cli-#{version}.jar" "$@"
        EOS
    end

    def install
        libexec.install Dir['*']
        (bin/'whisk').write startup_script('whisk')
    end
end
