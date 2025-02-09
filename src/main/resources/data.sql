# # /* 이 부분은 주석으로 처리된 블록입니다.
# #    여러 줄에 걸쳐 설명을 쓸 수 있습니다. */
# # INSERT INTO parking.car (carNumber, carType, entranceTime, isElectric)
# # VALUES ('testCarNumber', null, null, false)
# #
# CREATE TABLE IF NOT EXISTS parking.car
# (
#     id           BIGINT AUTO_INCREMENT PRIMARY KEY,
#     carNumber    VARCHAR(255),
#     carType      VARCHAR(255),
#     entranceTime DATETIME,
#     isElectric   BOOLEAN
# );
#
INSERT INTO parking.parkingHistory (deleteAt, entranceTime, exitTime, paymentType, carId, cardId,
                                    memberId, parkingZoneId, payId)
VALUES (null, '2025-02-09 17:02:39.000000', null, '1', 2, null, 2, 1, null)

#
