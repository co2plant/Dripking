# Git Convention

```bash
type :  subject

body 

footer
```
### type
- feat(ure) : 새로운 기능 추가
- fix : 버그 수정
- docs : 문서 내용 변경
- style : 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우 등
- refactor : 코드 리팩토링
- test : 테스트 코드 작성 
- chore : 빌드 수정, 패키지 매니저 설정, 운영 코드 변경이 없는 경우 등

### Subject
- 제목은 50자를 넘기지 않고 마침표를 찍지 않는다.
- 영문 표기 시 첫 글자는 대문자로 작성한다.
- 영문 표기 시 과거시제는 사용하지 않는다.
- 간결하고 요점만 서술해야한다.

### Body
- 선택사항이다.
- 본문은 최대한 상세히 적는다.
- 무엇을 왜 진행했는지 설명한다.
- 한 줄이 72자가 넘어가면 다음 문단으로 나눠 작성한다.

### Footer
- 선택사항이다.
- 이슈 트래커의 ID를 작성한다.
- 이슈 트래커의 ID를 작성할 때는 #을 붙여 작성한다.
- 여러 개의 이슈 트래커 ID를 작성할 때는 쉼표로 구분한다.
- 이슈 트래커 ID를 작성할 때는 이슈 트래커의 ID와 제목을 링크한다.
- 어떤 이슈와관련된 커밋인지 작성하면 좋다.(Resolves)
- 그 외 참고사항이 있는지 작성하면 좋다.(See also)

### Example
```bash
feat: Summarize changes in around 50 characters or less

More detailed explanatory text, if necessary. Wrap it to about 72
characters or so. In some contexts, the first line is treated as the
subject of the commit and the rest of the text as the body. The
blank line separating the summary from the body is critical (unless
you omit the body entirely); various tools like `log`, `shortlog`
and `rebase` can get confused if you run the two together.

Explain the problem that this commit is solving. Focus on why you
are making this change as opposed to how (the code explains that).
Are there side effects or other unintuitive consequences of this
change? Here's the place to explain them.

Further paragraphs come after blank lines.

 - Bullet points(•) are okay, too

 - Typically a hyphen or asterisk is used for the bullet, preceded
   by a single space, with blank lines in between, but conventions
   vary here

If you use an issue tracker, put references to them at the bottom,
like this:

Resolves: #123
See also: #456, #789
```

### 예시
```bash
feat: 50자 이내로 변경 사항 요약

필요한 경우 더 자세한 설명을 작성합니다. 약 72자 정도로 줄을 맞춥니다.
일부 상황에서는 첫 번째 줄이 커밋의 제목으로 처리되고 나머지 텍스트가
본문으로 처리됩니다. 요약과 본문을 구분하는 빈 줄이 중요합니다
(본문을 완전히 생략하지 않는 한); `log`, `shortlog`, `rebase`와 같은
다양한 도구가 두 줄을 함께 사용하면 혼동될 수 있습니다.

이 커밋이 해결하는 문제를 설명합니다. 이 변경을 왜 하는지에 초점을 맞추고
어떻게 하는지는 설명하지 않습니다 (코드가 이를 설명합니다).
이 변경의 부작용이나 다른 직관적이지 않은 결과가 있는지 설명합니다.

빈 줄 다음에 추가 단락이 옵니다.

 - 불릿 포인트(•)도 괜찮습니다

 - 일반적으로 불릿은 하이픈이나 별표를 사용하며, 앞에 한 칸의 공백을 두고
   불릿 사이에 빈 줄을 넣지만, 여기에는 다양한 관례가 있습니다

이슈 트래커를 사용하는 경우, 다음과 같이 하단에 참조를 넣습니다:

Resolves: #123
See also: #456, #789
```
[참고자료](https://github.com/gyoogle/tech-interview-for-developer/blob/master/ETC/Git%20Commit%20Message%20Convention.md)
</br>
[참고자료](https://udacity.github.io/git-styleguide/)

