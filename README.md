### Calling_App ###

### Complete:
- Code chuyển từ OutgoingCall và IncomingCall từ Kotlin sang Java
- Khác mạng ok: Từ nhà justxoai đến nhà dmq
- BoxChatActivity: Có cả incoming call và outgoing call: call cho thẳng đến ng dùng trong boxchat
(Account: ducduyvx - 1234567890; vietanhngx - 1234567890; quangminhdo - 1234567890)
- Feature: đg ở boxchat khác, 1 ng khác call đến vẫn hiện ai gọi đến

### Error :
- Error 1: gọi dc cho quang, nghe dc quang nói nhg quang ko nghe dc: có lẽ do laptop xoài có vde
- Error 2: Quang gọi cho xoài nhg ko hiển thị là có ng gọi nhg của quang thì có: chưa bt nguyên nhân

### Note:
+ Quang: cần quang test call từ đth và lap của quang

+ Quang dùng bài thì quang đổi cái intent trong MainActivity đến cái OutgoingCall nhé, do t up lần 2
nên nó có nhiều thay đổi

+ 2 file đấy là OutgoingCall và IncomingCall nhé

+ T sẽ test sao cho cno bé lại và tiện lại: set thành string

+ check build gradle còn firebase thì có lẽ chưa dùng vì nó lquan đến pushnoti cơ 

+ t chưa test hết nhg cái uses-permission và 1 vài cái indepent trong gradle

+ Chỉnh UI 