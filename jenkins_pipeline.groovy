pipeline {
    agent any

    stages {


        stage('Clone Git Repos')
        {
            steps {
                sh 'git clone https://github.com/mapj17/lean_testing.git'
                sh 'git clone https://github.com/mapj17/lean_testing_tools.git'

                sh 'echo "Lean Testing Repo Commit ID" > commit_ids.txt'
                sh 'git --git-dir lean_testing/.git log | awk \'/^commit/{count+=1} count<2 {print $0}\' >> commit_ids.txt'

                sh 'echo "Lean Testing Tools Repo Commit ID" >> commit_ids.txt'
                sh 'git --git-dir lean_testing_tools/.git log | awk \'/^commit/{count+=1} count<2 {print $0}\' >> commit_ids.txt'
            }
        }
        stage('Run Coverage') {
            steps {
                sh 'python3 -m coverage run -m unittest discover -s lean_testing/tests/'
                script{
                    PARSE_COVERAGE = sh(script: "/bin/bash -c 'python3 -m coverage report --omit=lean_testing/tests/*,/usr/* | tee coverage_output.txt'", returnStdout: true)
                    echo "${PARSE_COVERAGE}"
                }
                sh 'cp coverage_output.txt data.txt'
                sh 'gawk -i inplace -f lean_testing_tools/filter_coverage_output.awk data.txt'
                sh 'python3 lean_testing_tools/parse_and_write_coverage.py < data.txt'
                sh 'rm data.txt'
            }
        }
        stage('Run Mutation Tests') {
            steps {
                sh '/home/pi/.local/bin/mut.py --unit-test lean_testing/tests/test_*.py --target lean_testing/src -m | tee mutation_output.txt | python3 lean_testing_tools/parse_and_write_mutation.py'
            }
        }
        stage('Remove Git repos') {
            steps {
                sh 'rm -rf lean_testing'
                sh 'rm -rf lean_testing_tools'
            }
        }
        stage('Plot Data'){
            steps{
                plot csvFileName: 'plot-7a29e72b-234d-40bb-b2e0-1702339f1b4b.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'coverage.csv', inclusionFlag: 'OFF', url: '']], group: 'Coverage', numBuilds: '15', style: 'line', title: 'Statement Coverage', useDescr: true, yaxisMaximum: '1.05', yaxisMinimum: '0'
                plot csvFileName: 'plot-7a29e72b-234d-40bb-b2e0-1702339f1b4c.csv', csvSeries: [[displayTableFlag: false, exclusionValues: '', file: 'mutation_score.csv', inclusionFlag: 'OFF', url: '']], group: 'Mutation Score', numBuilds: '15', style: 'line', title: 'Mutation Score', useDescr: true, yaxisMaximum: '1.05', yaxisMinimum: '0'
            }
        }

    }
    post {
        always {
            archiveArtifacts artifacts: 'coverage_output.txt'
            archiveArtifacts artifacts: 'mutation_output.txt'
            archiveArtifacts artifacts: 'commit_ids.txt'
        }
    }
}
