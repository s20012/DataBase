# 1.はじめに
就職活動で企業説明会に参加していたときに、人事の人が話してる最中、大事なことがあったとき、メモを取ることが多々ありました。しかし、メモすることに集中すると書いている最中、話は集中できず、逆に話を聞くことに集中すると大事な内容をメモすることができないことがあり困っていました。そこで、話した内容を直接文章にし、あとからメモした内容を読み返すことができるアプリを開発しようと考えました。</br>
# 2.機能
・音声入力</br>
・テキスト化した文書の保存管理</br>
・テキスト翻訳機能</br>
# 3.使用環境
・Android Studio (使用言語はKotlin)</br>
・SQLite3</br>
・RecognizerIntent</br>
・百度翻译API ([APIキー](https://github.com/s20012/DataBase/blob/master/app/src/main/java/com/example/databasetest/EditActivity.kt)は自分のを使ってね)<br>
# 4.使用方法
①アプリのホーム画面右上の＋ボタンを押す

<img src="https://user-images.githubusercontent.com/66397337/211489832-d436d893-99c0-4be9-bce0-1337b6dfa73f.png" width="180px">

②タイトルを入力します。例として「おはよう」にします。

<img src="https://user-images.githubusercontent.com/66397337/214182264-8f61ee75-d927-44a7-a09c-744ab2df3b6a.png" width="180px">

③本文は画面の右上の音声入力ボタンを押し、音声入力画面が出てきた特、記録したい言葉を入力（おはようございますと言う）すると本文に反映されます。

<img src="https://user-images.githubusercontent.com/66397337/214184926-d7db9d97-1aff-4580-b077-5ab309802bc8.png" width="180px">

④右上のボタンを押すことにより新規メモを作成することができ、メモは日付で管理されます。

<img src="https://user-images.githubusercontent.com/66397337/214185605-5cecca5d-679a-4195-a0a2-616da0b52fdf.png" width="180px">

⑤メモ内容の翻訳の場合、任意のリストのメモを押しメモ内容確認画面に飛ぶと、画面下部に翻訳機能があるので好きな言語で翻訳します。

<img src="https://user-images.githubusercontent.com/66397337/214186837-907c697e-698b-4861-9196-9b3199154de0.png" width="180px">

⑥メモを編集したい場合、メモ内容確認画面の右上にあるペンアイコンを押してください。最初の音声入力画面へと飛び好きに編集することができます。（日付も最終編集日になります。）

<img src="https://user-images.githubusercontent.com/66397337/214189978-8b5b080e-bc86-416f-bce8-278a0ff7a2df.png" width="180px">

⑦不要になったメモはゴミ箱アイコンを押すことで削除することができます。

<img src="https://user-images.githubusercontent.com/66397337/214190511-41965c67-1f8b-4f02-860a-b39acef76dc8.png" width="180px">

