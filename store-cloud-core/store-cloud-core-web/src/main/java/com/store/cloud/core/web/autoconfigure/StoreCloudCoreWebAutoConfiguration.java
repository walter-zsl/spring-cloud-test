package com.store.cloud.core.web.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import com.store.cloud.core.web.error.GlobalRestExceptionAdvice;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import(GlobalRestExceptionAdvice.class)
public class StoreCloudCoreWebAutoConfiguration {}
