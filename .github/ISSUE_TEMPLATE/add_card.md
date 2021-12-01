name: 添加音乐外观
description: 为音乐分享添加外观
title: "[添加外观]"
labels: enhancement
body:
  - type: markdown
    attributes:
      value: |
        在反馈问题前，请确保本插件是在最新版。
  - type: textarea
    id: main_link
    attributes:
      label: 外观描述
      description: 这个外观是怎么样的，有哪些功能，配图
    validations:
      required: true
  - type: textarea
    id: why
    attributes:
      label: 为什么要添加这个外观
      description: 它有什么特别的功能目前其他外观不能实现？
    validations:
      required: true
  - type: textarea
    id: how
    attributes:
      label: 相关资料
      description: 这个外观的mirai码/分享方法/Mirai项目的相关文档
    validations:
      required: true
