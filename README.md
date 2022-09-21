# batchstudy202209

## todo
파일
- 파일 read 실패했- 파일 read 실패했을 때, 다음 read하는지 다시 해야하는지 확인
-- 다시 read해야함.
-- 만약 read한 곳 이후부터 하고 싶다면, ExecutionContext에 update할 때마다 readCount를 넣어놓고, 재실행했을 때, readCount만큼 skip.
-- chunk size를 1로 해야 정확히 실패한 곳부터 read하고, chunk size가 크면, chunk size 실패한 곳부터 실행하게 됨.
- 파일 관련 step 만들기. move, copy
- encoding 테스트
- fixed 파일 read, write

DB
- MyBatis 붙여보기 + iterator
- Read Write

트랜잭션
- 트랜잭션 테스트 (required, nested, new)

병렬처리
- 파티셔닝
- 배치 processor 병렬처리
- 스케쥴러 붙여보기
- 스케쥴러로 병렬 job 호출하기
- Step 병렬 플로우

