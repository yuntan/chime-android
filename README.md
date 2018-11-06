Chime (Android)
===============
Increase your time consciousness.

詳細
----
### `AlarmManager` に登録するアラーム
1. `AlarmReceiver` を約15分おきに呼び出すための繰り返しアラーム．アラーム間隔はシステムにより調整されるので不確定．
2. `TTSService` を各時間の0分・15分・30分・45分に呼び出すためのアラーム．アラームのタイミングは1分のウィンドウで指定．

ターゲットが同じ `Intent` のアラームを設定しようとすると，前のアラーム設定がキャンセルされてしまう．2つのアラームを同時刻に設定すると前者が後者をキャンセルしてしまうことがあるので，時刻をずらして登録する．

### クラス
- `App`: `Application` アプリ起動時にアラーム1を設定する．
- `AlarmReceiver`: `BroadcastReceiver` 端末スリープ時を除き約15分おきにアラーム2を設定する．
- `TTSService`: `Service` 時刻に対応するテキストを読み上げる．

### 参考
- [Android Scheduling Background Services( A developer’s nightmare)](https://medium.com/mindorks/android-scheduling-background-services-a-developers-nightmare-c573807c2705)
- [Android の AlarmManager を改めて整理してみる - Qiita](https://qiita.com/upft_rkoshida/items/8149605f751137b4c21c)
- [Y.A.M の 雑記帳: AlarmManager](http://y-anz-m.blogspot.com/2011/03/alarmmanager.html)
