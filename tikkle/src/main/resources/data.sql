INSERT INTO badge(code,title,description,icon,condition_type,threshold,keyword) VALUES
 ('FIRST_SAVING','첫 저축!','첫 기록을 했어요 🎉','🌱','SAVINGS_COUNT',1,NULL)
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO badge(code,title,description,icon,condition_type,threshold,keyword) VALUES
 ('COFFEE_10','커피 10번 참기','커피를 10번 참았어요!','☕️','MEMO_COUNT',10,'커피')
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO badge(code,title,description,icon,condition_type,threshold,keyword) VALUES
 ('SAVINGS_10','10회 저축','저축 10회 달성!','🪙','SAVINGS_COUNT',10,NULL)
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO badge(code,title,description,icon,condition_type,threshold,keyword) VALUES
 ('SUM_100K','10만원 모으기','누적 100,000원 달성','💰','SAVINGS_SUM',100000,NULL)
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO badge(code,title,description,icon,condition_type,threshold,keyword) VALUES
 ('GOAL_DONE','목표 달성!','한 개 이상의 목표를 달성했어요','🏁','GOAL_COMPLETED',1,NULL)
ON DUPLICATE KEY UPDATE code=code;