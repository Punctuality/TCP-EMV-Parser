package site.sergeyfedorov.model;

import site.sergeyfedorov.util.limits.LimitNumber;

sealed interface EMVTagFormat permits
    BinaryEMVTag,
    NumericEMVTag,
    CompressedNumericEMVTag,
    AlphaNumericSpecialEMVTag,
    AlphaNumericEMVTag,
    AlphaEMVTag,
    DOLTag {}

record BinaryEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record NumericEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record CompressedNumericEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record AlphaNumericEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record AlphaNumericSpecialEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record AlphaEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat {}

record DOLTag() implements EMVTagFormat {}