#!/bin/bash

# JMeter 스트레스 테스트 자동 실행 스크립트
# 사용법: ./run-test.sh

echo "🚀 JMeter 스트레스 테스트 시작..."

# 1. 기존 결과 파일 정리
echo "📁 기존 결과 파일 정리 중..."
rm -f jmeter/test-results.jtl
rm -rf jmeter-report

# 2. JMeter 테스트 실행
echo "⚡ JMeter 테스트 실행 중..."
jmeter -n -t jmeter/api-stress-test.jmx -l jmeter/test-results.jtl

# 3. HTML 리포트 생성
echo "📊 HTML 리포트 생성 중..."
jmeter -g jmeter/test-results.jtl -o jmeter-report

# 4. 결과 확인
echo "✅ 테스트 완료!"
echo "📈 리포트 확인: open jmeter-report/index.html"

# 자동으로 리포트 열기 (macOS)
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "🌐 브라우저에서 리포트 자동 실행..."
    open jmeter-report/index.html
fi

echo "🎉 테스트 결과가 준비되었습니다!"