// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: app_pull_msg.proto

#define INTERNAL_SUPPRESS_PROTOBUF_FIELD_DEPRECATION
#include "app_pull_msg.pb.h"

#include <algorithm>

#include <google/protobuf/stubs/common.h>
#include <google/protobuf/stubs/once.h>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/wire_format_lite_inl.h>
#include <google/protobuf/descriptor.h>
#include <google/protobuf/generated_message_reflection.h>
#include <google/protobuf/reflection_ops.h>
#include <google/protobuf/wire_format.h>
// @@protoc_insertion_point(includes)

namespace StatPullMsgProto {

namespace {

const ::google_public::protobuf::Descriptor* MsgBody_descriptor_ = NULL;
const ::google_public::protobuf::internal::GeneratedMessageReflection*
  MsgBody_reflection_ = NULL;
const ::google_public::protobuf::Descriptor* AppPullMsgRequest_descriptor_ = NULL;
const ::google_public::protobuf::internal::GeneratedMessageReflection*
  AppPullMsgRequest_reflection_ = NULL;
const ::google_public::protobuf::Descriptor* AppPullMsgResponse_descriptor_ = NULL;
const ::google_public::protobuf::internal::GeneratedMessageReflection*
  AppPullMsgResponse_reflection_ = NULL;

}  // namespace


void protobuf_AssignDesc_app_5fpull_5fmsg_2eproto() {
  protobuf_AddDesc_app_5fpull_5fmsg_2eproto();
  const ::google_public::protobuf::FileDescriptor* file =
    ::google_public::protobuf::DescriptorPool::generated_pool()->FindFileByName(
      "app_pull_msg.proto");
  GOOGLE_CHECK(file != NULL);
  MsgBody_descriptor_ = file->message_type(0);
  static const int MsgBody_offsets_[3] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(MsgBody, msg_id_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(MsgBody, title_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(MsgBody, content_),
  };
  MsgBody_reflection_ =
    new ::google_public::protobuf::internal::GeneratedMessageReflection(
      MsgBody_descriptor_,
      MsgBody::default_instance_,
      MsgBody_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(MsgBody, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(MsgBody, _unknown_fields_),
      -1,
      ::google_public::protobuf::DescriptorPool::generated_pool(),
      ::google_public::protobuf::MessageFactory::generated_factory(),
      sizeof(MsgBody));
  AppPullMsgRequest_descriptor_ = file->message_type(1);
  static const int AppPullMsgRequest_offsets_[3] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgRequest, user_name_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgRequest, msg_id_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgRequest, token_),
  };
  AppPullMsgRequest_reflection_ =
    new ::google_public::protobuf::internal::GeneratedMessageReflection(
      AppPullMsgRequest_descriptor_,
      AppPullMsgRequest::default_instance_,
      AppPullMsgRequest_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgRequest, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgRequest, _unknown_fields_),
      -1,
      ::google_public::protobuf::DescriptorPool::generated_pool(),
      ::google_public::protobuf::MessageFactory::generated_factory(),
      sizeof(AppPullMsgRequest));
  AppPullMsgResponse_descriptor_ = file->message_type(2);
  static const int AppPullMsgResponse_offsets_[2] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgResponse, ret_),
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgResponse, msg_),
  };
  AppPullMsgResponse_reflection_ =
    new ::google_public::protobuf::internal::GeneratedMessageReflection(
      AppPullMsgResponse_descriptor_,
      AppPullMsgResponse::default_instance_,
      AppPullMsgResponse_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgResponse, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(AppPullMsgResponse, _unknown_fields_),
      -1,
      ::google_public::protobuf::DescriptorPool::generated_pool(),
      ::google_public::protobuf::MessageFactory::generated_factory(),
      sizeof(AppPullMsgResponse));
}

namespace {

GOOGLE_PROTOBUF_DECLARE_ONCE(protobuf_AssignDescriptors_once_);
inline void protobuf_AssignDescriptorsOnce() {
  ::google_public::protobuf::GoogleOnceInit(&protobuf_AssignDescriptors_once_,
                 &protobuf_AssignDesc_app_5fpull_5fmsg_2eproto);
}

void protobuf_RegisterTypes(const ::std::string&) {
  protobuf_AssignDescriptorsOnce();
  ::google_public::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    MsgBody_descriptor_, &MsgBody::default_instance());
  ::google_public::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    AppPullMsgRequest_descriptor_, &AppPullMsgRequest::default_instance());
  ::google_public::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    AppPullMsgResponse_descriptor_, &AppPullMsgResponse::default_instance());
}

}  // namespace

void protobuf_ShutdownFile_app_5fpull_5fmsg_2eproto() {
  delete MsgBody::default_instance_;
  delete MsgBody_reflection_;
  delete AppPullMsgRequest::default_instance_;
  delete AppPullMsgRequest_reflection_;
  delete AppPullMsgResponse::default_instance_;
  delete AppPullMsgResponse_reflection_;
}

void protobuf_AddDesc_app_5fpull_5fmsg_2eproto() {
  static bool already_here = false;
  if (already_here) return;
  already_here = true;
  GOOGLE_PROTOBUF_VERIFY_VERSION;

  ::google_public::protobuf::DescriptorPool::InternalAddGeneratedFile(
    "\n\022app_pull_msg.proto\022\020StatPullMsgProto\"9"
    "\n\007MsgBody\022\016\n\006msg_id\030\001 \002(\t\022\r\n\005title\030\002 \002(\t"
    "\022\017\n\007content\030\003 \002(\t\"E\n\021AppPullMsgRequest\022\021"
    "\n\tuser_name\030\001 \002(\t\022\016\n\006msg_id\030\002 \003(\t\022\r\n\005tok"
    "en\030\003 \001(\t\"I\n\022AppPullMsgResponse\022\013\n\003ret\030\001 "
    "\002(\r\022&\n\003msg\030\002 \003(\0132\031.StatPullMsgProto.MsgB"
    "odyB\020\n\016stat_app_proto", 261);
  ::google_public::protobuf::MessageFactory::InternalRegisterGeneratedFile(
    "app_pull_msg.proto", &protobuf_RegisterTypes);
  MsgBody::default_instance_ = new MsgBody();
  AppPullMsgRequest::default_instance_ = new AppPullMsgRequest();
  AppPullMsgResponse::default_instance_ = new AppPullMsgResponse();
  MsgBody::default_instance_->InitAsDefaultInstance();
  AppPullMsgRequest::default_instance_->InitAsDefaultInstance();
  AppPullMsgResponse::default_instance_->InitAsDefaultInstance();
  ::google_public::protobuf::internal::OnShutdown(&protobuf_ShutdownFile_app_5fpull_5fmsg_2eproto);
}

// Force AddDescriptors() to be called at static initialization time.
struct StaticDescriptorInitializer_app_5fpull_5fmsg_2eproto {
  StaticDescriptorInitializer_app_5fpull_5fmsg_2eproto() {
    protobuf_AddDesc_app_5fpull_5fmsg_2eproto();
  }
} static_descriptor_initializer_app_5fpull_5fmsg_2eproto_;

// ===================================================================

#ifndef _MSC_VER
const int MsgBody::kMsgIdFieldNumber;
const int MsgBody::kTitleFieldNumber;
const int MsgBody::kContentFieldNumber;
#endif  // !_MSC_VER

MsgBody::MsgBody()
  : ::google_public::protobuf::Message() {
  SharedCtor();
}

void MsgBody::InitAsDefaultInstance() {
}

MsgBody::MsgBody(const MsgBody& from)
  : ::google_public::protobuf::Message() {
  SharedCtor();
  MergeFrom(from);
}

void MsgBody::SharedCtor() {
  _cached_size_ = 0;
  msg_id_ = const_cast< ::std::string*>(&::google_public::protobuf::internal::kEmptyString);
  title_ = const_cast< ::std::string*>(&::google_public::protobuf::internal::kEmptyString);
  content_ = const_cast< ::std::string*>(&::google_public::protobuf::internal::kEmptyString);
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

MsgBody::~MsgBody() {
  SharedDtor();
}

void MsgBody::SharedDtor() {
  if (msg_id_ != &::google_public::protobuf::internal::kEmptyString) {
    delete msg_id_;
  }
  if (title_ != &::google_public::protobuf::internal::kEmptyString) {
    delete title_;
  }
  if (content_ != &::google_public::protobuf::internal::kEmptyString) {
    delete content_;
  }
  if (this != default_instance_) {
  }
}

void MsgBody::SetCachedSize(int size) const {
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
}
const ::google_public::protobuf::Descriptor* MsgBody::descriptor() {
  protobuf_AssignDescriptorsOnce();
  return MsgBody_descriptor_;
}

const MsgBody& MsgBody::default_instance() {
  if (default_instance_ == NULL) protobuf_AddDesc_app_5fpull_5fmsg_2eproto();
  return *default_instance_;
}

MsgBody* MsgBody::default_instance_ = NULL;

MsgBody* MsgBody::New() const {
  return new MsgBody;
}

void MsgBody::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (has_msg_id()) {
      if (msg_id_ != &::google_public::protobuf::internal::kEmptyString) {
        msg_id_->clear();
      }
    }
    if (has_title()) {
      if (title_ != &::google_public::protobuf::internal::kEmptyString) {
        title_->clear();
      }
    }
    if (has_content()) {
      if (content_ != &::google_public::protobuf::internal::kEmptyString) {
        content_->clear();
      }
    }
  }
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool MsgBody::MergePartialFromCodedStream(
    ::google_public::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google_public::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google_public::protobuf::internal::WireFormatLite::GetTagFieldNumber(tag)) {
      // required string msg_id = 1;
      case 1: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_msg_id()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->msg_id().data(), this->msg_id().length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(18)) goto parse_title;
        break;
      }

      // required string title = 2;
      case 2: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_title:
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_title()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->title().data(), this->title().length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(26)) goto parse_content;
        break;
      }

      // required string content = 3;
      case 3: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_content:
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_content()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->content().data(), this->content().length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectAtEnd()) return true;
        break;
      }

      default: {
      handle_uninterpreted:
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google_public::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

void MsgBody::SerializeWithCachedSizes(
    ::google_public::protobuf::io::CodedOutputStream* output) const {
  // required string msg_id = 1;
  if (has_msg_id()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->msg_id().data(), this->msg_id().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      1, this->msg_id(), output);
  }

  // required string title = 2;
  if (has_title()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->title().data(), this->title().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      2, this->title(), output);
  }

  // required string content = 3;
  if (has_content()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->content().data(), this->content().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      3, this->content(), output);
  }

  if (!unknown_fields().empty()) {
    ::google_public::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output);
  }
}

::google_public::protobuf::uint8* MsgBody::SerializeWithCachedSizesToArray(
    ::google_public::protobuf::uint8* target) const {
  // required string msg_id = 1;
  if (has_msg_id()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->msg_id().data(), this->msg_id().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google_public::protobuf::internal::WireFormatLite::WriteStringToArray(
        1, this->msg_id(), target);
  }

  // required string title = 2;
  if (has_title()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->title().data(), this->title().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google_public::protobuf::internal::WireFormatLite::WriteStringToArray(
        2, this->title(), target);
  }

  // required string content = 3;
  if (has_content()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->content().data(), this->content().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google_public::protobuf::internal::WireFormatLite::WriteStringToArray(
        3, this->content(), target);
  }

  if (!unknown_fields().empty()) {
    target = ::google_public::protobuf::internal::WireFormat::SerializeUnknownFieldsToArray(
        unknown_fields(), target);
  }
  return target;
}

int MsgBody::ByteSize() const {
  int total_size = 0;

  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // required string msg_id = 1;
    if (has_msg_id()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::StringSize(
          this->msg_id());
    }

    // required string title = 2;
    if (has_title()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::StringSize(
          this->title());
    }

    // required string content = 3;
    if (has_content()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::StringSize(
          this->content());
    }

  }
  if (!unknown_fields().empty()) {
    total_size +=
      ::google_public::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = total_size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
  return total_size;
}

void MsgBody::MergeFrom(const ::google_public::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const MsgBody* source =
    ::google_public::protobuf::internal::dynamic_cast_if_available<const MsgBody*>(
      &from);
  if (source == NULL) {
    ::google_public::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void MsgBody::MergeFrom(const MsgBody& from) {
  GOOGLE_CHECK_NE(&from, this);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from.has_msg_id()) {
      set_msg_id(from.msg_id());
    }
    if (from.has_title()) {
      set_title(from.title());
    }
    if (from.has_content()) {
      set_content(from.content());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void MsgBody::CopyFrom(const ::google_public::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void MsgBody::CopyFrom(const MsgBody& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

bool MsgBody::IsInitialized() const {
  if ((_has_bits_[0] & 0x00000007) != 0x00000007) return false;

  return true;
}

void MsgBody::Swap(MsgBody* other) {
  if (other != this) {
    std::swap(msg_id_, other->msg_id_);
    std::swap(title_, other->title_);
    std::swap(content_, other->content_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

::google_public::protobuf::Metadata MsgBody::GetMetadata() const {
  protobuf_AssignDescriptorsOnce();
  ::google_public::protobuf::Metadata metadata;
  metadata.descriptor = MsgBody_descriptor_;
  metadata.reflection = MsgBody_reflection_;
  return metadata;
}


// ===================================================================

#ifndef _MSC_VER
const int AppPullMsgRequest::kUserNameFieldNumber;
const int AppPullMsgRequest::kMsgIdFieldNumber;
const int AppPullMsgRequest::kTokenFieldNumber;
#endif  // !_MSC_VER

AppPullMsgRequest::AppPullMsgRequest()
  : ::google_public::protobuf::Message() {
  SharedCtor();
}

void AppPullMsgRequest::InitAsDefaultInstance() {
}

AppPullMsgRequest::AppPullMsgRequest(const AppPullMsgRequest& from)
  : ::google_public::protobuf::Message() {
  SharedCtor();
  MergeFrom(from);
}

void AppPullMsgRequest::SharedCtor() {
  _cached_size_ = 0;
  user_name_ = const_cast< ::std::string*>(&::google_public::protobuf::internal::kEmptyString);
  token_ = const_cast< ::std::string*>(&::google_public::protobuf::internal::kEmptyString);
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

AppPullMsgRequest::~AppPullMsgRequest() {
  SharedDtor();
}

void AppPullMsgRequest::SharedDtor() {
  if (user_name_ != &::google_public::protobuf::internal::kEmptyString) {
    delete user_name_;
  }
  if (token_ != &::google_public::protobuf::internal::kEmptyString) {
    delete token_;
  }
  if (this != default_instance_) {
  }
}

void AppPullMsgRequest::SetCachedSize(int size) const {
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
}
const ::google_public::protobuf::Descriptor* AppPullMsgRequest::descriptor() {
  protobuf_AssignDescriptorsOnce();
  return AppPullMsgRequest_descriptor_;
}

const AppPullMsgRequest& AppPullMsgRequest::default_instance() {
  if (default_instance_ == NULL) protobuf_AddDesc_app_5fpull_5fmsg_2eproto();
  return *default_instance_;
}

AppPullMsgRequest* AppPullMsgRequest::default_instance_ = NULL;

AppPullMsgRequest* AppPullMsgRequest::New() const {
  return new AppPullMsgRequest;
}

void AppPullMsgRequest::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (has_user_name()) {
      if (user_name_ != &::google_public::protobuf::internal::kEmptyString) {
        user_name_->clear();
      }
    }
    if (has_token()) {
      if (token_ != &::google_public::protobuf::internal::kEmptyString) {
        token_->clear();
      }
    }
  }
  msg_id_.Clear();
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool AppPullMsgRequest::MergePartialFromCodedStream(
    ::google_public::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google_public::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google_public::protobuf::internal::WireFormatLite::GetTagFieldNumber(tag)) {
      // required string user_name = 1;
      case 1: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_user_name()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->user_name().data(), this->user_name().length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(18)) goto parse_msg_id;
        break;
      }

      // repeated string msg_id = 2;
      case 2: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_msg_id:
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->add_msg_id()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->msg_id(this->msg_id_size() - 1).data(),
            this->msg_id(this->msg_id_size() - 1).length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(18)) goto parse_msg_id;
        if (input->ExpectTag(26)) goto parse_token;
        break;
      }

      // optional string token = 3;
      case 3: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_token:
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadString(
                input, this->mutable_token()));
          ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
            this->token().data(), this->token().length(),
            ::google_public::protobuf::internal::WireFormat::PARSE);
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectAtEnd()) return true;
        break;
      }

      default: {
      handle_uninterpreted:
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google_public::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

void AppPullMsgRequest::SerializeWithCachedSizes(
    ::google_public::protobuf::io::CodedOutputStream* output) const {
  // required string user_name = 1;
  if (has_user_name()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->user_name().data(), this->user_name().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      1, this->user_name(), output);
  }

  // repeated string msg_id = 2;
  for (int i = 0; i < this->msg_id_size(); i++) {
  ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
    this->msg_id(i).data(), this->msg_id(i).length(),
    ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      2, this->msg_id(i), output);
  }

  // optional string token = 3;
  if (has_token()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->token().data(), this->token().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    ::google_public::protobuf::internal::WireFormatLite::WriteString(
      3, this->token(), output);
  }

  if (!unknown_fields().empty()) {
    ::google_public::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output);
  }
}

::google_public::protobuf::uint8* AppPullMsgRequest::SerializeWithCachedSizesToArray(
    ::google_public::protobuf::uint8* target) const {
  // required string user_name = 1;
  if (has_user_name()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->user_name().data(), this->user_name().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google_public::protobuf::internal::WireFormatLite::WriteStringToArray(
        1, this->user_name(), target);
  }

  // repeated string msg_id = 2;
  for (int i = 0; i < this->msg_id_size(); i++) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->msg_id(i).data(), this->msg_id(i).length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target = ::google_public::protobuf::internal::WireFormatLite::
      WriteStringToArray(2, this->msg_id(i), target);
  }

  // optional string token = 3;
  if (has_token()) {
    ::google_public::protobuf::internal::WireFormat::VerifyUTF8String(
      this->token().data(), this->token().length(),
      ::google_public::protobuf::internal::WireFormat::SERIALIZE);
    target =
      ::google_public::protobuf::internal::WireFormatLite::WriteStringToArray(
        3, this->token(), target);
  }

  if (!unknown_fields().empty()) {
    target = ::google_public::protobuf::internal::WireFormat::SerializeUnknownFieldsToArray(
        unknown_fields(), target);
  }
  return target;
}

int AppPullMsgRequest::ByteSize() const {
  int total_size = 0;

  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // required string user_name = 1;
    if (has_user_name()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::StringSize(
          this->user_name());
    }

    // optional string token = 3;
    if (has_token()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::StringSize(
          this->token());
    }

  }
  // repeated string msg_id = 2;
  total_size += 1 * this->msg_id_size();
  for (int i = 0; i < this->msg_id_size(); i++) {
    total_size += ::google_public::protobuf::internal::WireFormatLite::StringSize(
      this->msg_id(i));
  }

  if (!unknown_fields().empty()) {
    total_size +=
      ::google_public::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = total_size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
  return total_size;
}

void AppPullMsgRequest::MergeFrom(const ::google_public::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const AppPullMsgRequest* source =
    ::google_public::protobuf::internal::dynamic_cast_if_available<const AppPullMsgRequest*>(
      &from);
  if (source == NULL) {
    ::google_public::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void AppPullMsgRequest::MergeFrom(const AppPullMsgRequest& from) {
  GOOGLE_CHECK_NE(&from, this);
  msg_id_.MergeFrom(from.msg_id_);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from.has_user_name()) {
      set_user_name(from.user_name());
    }
    if (from.has_token()) {
      set_token(from.token());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void AppPullMsgRequest::CopyFrom(const ::google_public::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void AppPullMsgRequest::CopyFrom(const AppPullMsgRequest& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

bool AppPullMsgRequest::IsInitialized() const {
  if ((_has_bits_[0] & 0x00000001) != 0x00000001) return false;

  return true;
}

void AppPullMsgRequest::Swap(AppPullMsgRequest* other) {
  if (other != this) {
    std::swap(user_name_, other->user_name_);
    msg_id_.Swap(&other->msg_id_);
    std::swap(token_, other->token_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

::google_public::protobuf::Metadata AppPullMsgRequest::GetMetadata() const {
  protobuf_AssignDescriptorsOnce();
  ::google_public::protobuf::Metadata metadata;
  metadata.descriptor = AppPullMsgRequest_descriptor_;
  metadata.reflection = AppPullMsgRequest_reflection_;
  return metadata;
}


// ===================================================================

#ifndef _MSC_VER
const int AppPullMsgResponse::kRetFieldNumber;
const int AppPullMsgResponse::kMsgFieldNumber;
#endif  // !_MSC_VER

AppPullMsgResponse::AppPullMsgResponse()
  : ::google_public::protobuf::Message() {
  SharedCtor();
}

void AppPullMsgResponse::InitAsDefaultInstance() {
}

AppPullMsgResponse::AppPullMsgResponse(const AppPullMsgResponse& from)
  : ::google_public::protobuf::Message() {
  SharedCtor();
  MergeFrom(from);
}

void AppPullMsgResponse::SharedCtor() {
  _cached_size_ = 0;
  ret_ = 0u;
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

AppPullMsgResponse::~AppPullMsgResponse() {
  SharedDtor();
}

void AppPullMsgResponse::SharedDtor() {
  if (this != default_instance_) {
  }
}

void AppPullMsgResponse::SetCachedSize(int size) const {
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
}
const ::google_public::protobuf::Descriptor* AppPullMsgResponse::descriptor() {
  protobuf_AssignDescriptorsOnce();
  return AppPullMsgResponse_descriptor_;
}

const AppPullMsgResponse& AppPullMsgResponse::default_instance() {
  if (default_instance_ == NULL) protobuf_AddDesc_app_5fpull_5fmsg_2eproto();
  return *default_instance_;
}

AppPullMsgResponse* AppPullMsgResponse::default_instance_ = NULL;

AppPullMsgResponse* AppPullMsgResponse::New() const {
  return new AppPullMsgResponse;
}

void AppPullMsgResponse::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    ret_ = 0u;
  }
  msg_.Clear();
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool AppPullMsgResponse::MergePartialFromCodedStream(
    ::google_public::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google_public::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google_public::protobuf::internal::WireFormatLite::GetTagFieldNumber(tag)) {
      // required uint32 ret = 1;
      case 1: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_VARINT) {
          DO_((::google_public::protobuf::internal::WireFormatLite::ReadPrimitive<
                   ::google_public::protobuf::uint32, ::google_public::protobuf::internal::WireFormatLite::TYPE_UINT32>(
                 input, &ret_)));
          set_has_ret();
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(18)) goto parse_msg;
        break;
      }

      // repeated .StatPullMsgProto.MsgBody msg = 2;
      case 2: {
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_LENGTH_DELIMITED) {
         parse_msg:
          DO_(::google_public::protobuf::internal::WireFormatLite::ReadMessageNoVirtual(
                input, add_msg()));
        } else {
          goto handle_uninterpreted;
        }
        if (input->ExpectTag(18)) goto parse_msg;
        if (input->ExpectAtEnd()) return true;
        break;
      }

      default: {
      handle_uninterpreted:
        if (::google_public::protobuf::internal::WireFormatLite::GetTagWireType(tag) ==
            ::google_public::protobuf::internal::WireFormatLite::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google_public::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

void AppPullMsgResponse::SerializeWithCachedSizes(
    ::google_public::protobuf::io::CodedOutputStream* output) const {
  // required uint32 ret = 1;
  if (has_ret()) {
    ::google_public::protobuf::internal::WireFormatLite::WriteUInt32(1, this->ret(), output);
  }

  // repeated .StatPullMsgProto.MsgBody msg = 2;
  for (int i = 0; i < this->msg_size(); i++) {
    ::google_public::protobuf::internal::WireFormatLite::WriteMessageMaybeToArray(
      2, this->msg(i), output);
  }

  if (!unknown_fields().empty()) {
    ::google_public::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output);
  }
}

::google_public::protobuf::uint8* AppPullMsgResponse::SerializeWithCachedSizesToArray(
    ::google_public::protobuf::uint8* target) const {
  // required uint32 ret = 1;
  if (has_ret()) {
    target = ::google_public::protobuf::internal::WireFormatLite::WriteUInt32ToArray(1, this->ret(), target);
  }

  // repeated .StatPullMsgProto.MsgBody msg = 2;
  for (int i = 0; i < this->msg_size(); i++) {
    target = ::google_public::protobuf::internal::WireFormatLite::
      WriteMessageNoVirtualToArray(
        2, this->msg(i), target);
  }

  if (!unknown_fields().empty()) {
    target = ::google_public::protobuf::internal::WireFormat::SerializeUnknownFieldsToArray(
        unknown_fields(), target);
  }
  return target;
}

int AppPullMsgResponse::ByteSize() const {
  int total_size = 0;

  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // required uint32 ret = 1;
    if (has_ret()) {
      total_size += 1 +
        ::google_public::protobuf::internal::WireFormatLite::UInt32Size(
          this->ret());
    }

  }
  // repeated .StatPullMsgProto.MsgBody msg = 2;
  total_size += 1 * this->msg_size();
  for (int i = 0; i < this->msg_size(); i++) {
    total_size +=
      ::google_public::protobuf::internal::WireFormatLite::MessageSizeNoVirtual(
        this->msg(i));
  }

  if (!unknown_fields().empty()) {
    total_size +=
      ::google_public::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  GOOGLE_SAFE_CONCURRENT_WRITES_BEGIN();
  _cached_size_ = total_size;
  GOOGLE_SAFE_CONCURRENT_WRITES_END();
  return total_size;
}

void AppPullMsgResponse::MergeFrom(const ::google_public::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const AppPullMsgResponse* source =
    ::google_public::protobuf::internal::dynamic_cast_if_available<const AppPullMsgResponse*>(
      &from);
  if (source == NULL) {
    ::google_public::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void AppPullMsgResponse::MergeFrom(const AppPullMsgResponse& from) {
  GOOGLE_CHECK_NE(&from, this);
  msg_.MergeFrom(from.msg_);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from.has_ret()) {
      set_ret(from.ret());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void AppPullMsgResponse::CopyFrom(const ::google_public::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void AppPullMsgResponse::CopyFrom(const AppPullMsgResponse& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

bool AppPullMsgResponse::IsInitialized() const {
  if ((_has_bits_[0] & 0x00000001) != 0x00000001) return false;

  for (int i = 0; i < msg_size(); i++) {
    if (!this->msg(i).IsInitialized()) return false;
  }
  return true;
}

void AppPullMsgResponse::Swap(AppPullMsgResponse* other) {
  if (other != this) {
    std::swap(ret_, other->ret_);
    msg_.Swap(&other->msg_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

::google_public::protobuf::Metadata AppPullMsgResponse::GetMetadata() const {
  protobuf_AssignDescriptorsOnce();
  ::google_public::protobuf::Metadata metadata;
  metadata.descriptor = AppPullMsgResponse_descriptor_;
  metadata.reflection = AppPullMsgResponse_reflection_;
  return metadata;
}


// @@protoc_insertion_point(namespace_scope)

}  // namespace StatPullMsgProto

// @@protoc_insertion_point(global_scope)
