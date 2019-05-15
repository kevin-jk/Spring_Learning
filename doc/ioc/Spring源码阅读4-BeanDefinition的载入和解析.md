在上一节中，我们读源码到Resource的定位部分。 当DefaultResourceLoader返回Resource后，在AbstractBeanDefinitionReader中会对得到的Resource进行读取和解析。 下面是AbstractBeanDefinitionReader中对应的方法：

```java
public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
      ResourceLoader resourceLoader = getResourceLoader();
      if (resourceLoader == null) {
         throw new BeanDefinitionStoreException(
               "Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
      }

      if (resourceLoader instanceof ResourcePatternResolver) {
         // Resource pattern matching available.
         try {
                // 获取Resource, 终于发现了，我们这一节的重点， Resource的定位。 下面我们进去看看，到底怎么加载的
            Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
            // BeanDefinition 的载入和解析
            int count = loadBeanDefinitions(resources);
            if (actualResources != null) {
               Collections.addAll(actualResources, resources);
            }
            if (logger.isTraceEnabled()) {
               logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
            }
            return count;
         }
         catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                  "Could not resolve bean definition resource pattern [" + location + "]", ex);
         }
      }
      else {
         // Can only load single resources by absolute URL.
         Resource resource = resourceLoader.getResource(location);
         int count = loadBeanDefinitions(resource);
         if (actualResources != null) {
            actualResources.add(resource);
         }
         if (logger.isTraceEnabled()) {
            logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
         }
         return count;
      }
   }
```

下面我们进入到loadBeanDefinitions中，发现会走到XmlBeanDefinitionReader类中的loadBeanDefinitions方法：

```java
public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Loading XML bean definitions from " + encodedResource);
		}

		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (currentResources == null) {
			currentResources = new HashSet<>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			throw new BeanDefinitionStoreException(
					"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
            // 得到XML文件， 并获取IO流inputStream
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
                // 具体的读取过程，从xml文件载入BeanDefinition的过程
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			}
			finally {
				inputStream.close();
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}
```

然后会到doLoadBeanDefinitions方法中：

```java
protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource)
			throws BeanDefinitionStoreException {

		try {
            // 读取XML文件的Doucument对象，这个解析过程是由DocumentLoader来完成 
			Document doc = doLoadDocument(inputSource, resource);
            
            // 启动对BeanDefinition的详细解析过程，使用Spring的 Bean配置规则
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		}
		catch (BeanDefinitionStoreException ex) {
			throw ex;
		}
		catch (SAXParseException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),
					"Line " + ex.getLineNumber() + " in XML document from " + resource + " is invalid", ex);
		}
		catch (SAXException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),
					"XML document from " + resource + " is invalid", ex);
		}
		catch (ParserConfigurationException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"Parser configuration exception parsing XML from " + resource, ex);
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"IOException parsing XML document from " + resource, ex);
		}
		catch (Throwable ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"Unexpected exception parsing XML document from " + resource, ex);
		}
	}
```

由此可以看到，首先是利用XML解析器获得document对象， 然后再解析Bean规则完成对BeanDefinition的载入。下面我们看registerBeanDefinitions过程，最终可以到DefaultBeanDefinitionDocumentReader类中的doRegisterBeanDefinitions方法：

```java
protected void doRegisterBeanDefinitions(Element root) {
		// Any nested <beans> elements will cause recursion in this method. In
		// order to propagate and preserve <beans> default-* attributes correctly,
		// keep track of the current (parent) delegate, which may be null. Create
		// the new (child) delegate with a reference to the parent for fallback purposes,
		// then ultimately reset this.delegate back to its original (parent) reference.
		// this behavior emulates a stack of delegates without actually necessitating one.
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createDelegate(getReaderContext(), root, parent);

		if (this.delegate.isDefaultNamespace(root)) {
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
			if (StringUtils.hasText(profileSpec)) {
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
				// We cannot use Profiles.of(...) since profile expressions are not supported
				// in XML config. See SPR-12458 for details.
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		preProcessXml(root);
        // 具体的处理过程是由BeanDefinitionParserDelegate来完成的
		parseBeanDefinitions(root, this.delegate);
		postProcessXml(root);

		this.delegate = parent;
	}
```

看下parseBeanDefinitions方法：

```java
protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node instanceof Element) {
					Element ele = (Element) node;
					if (delegate.isDefaultNamespace(ele)) {
                        // 解析默认元素
						parseDefaultElement(ele, delegate);
					}
					else {
                         // 解析用户定义元素，如注解的解析
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
            // 解析用户定义元素，如注解的解析
			delegate.parseCustomElement(root);
		}
	}
```

通过上面我们看到，此时会对具体的元素进行解析，下面我们仅仅分析parseDefaultElement方法，如对<bean>标签的解析。具体的看如：

```java
private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
		    // Bean标签的解析过程
			processBeanDefinition(ele, delegate);
		}
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse
			doRegisterBeanDefinitions(ele);
		}
	}
```

重点关注processBeanDefinition方法，具体的如下：

```java
protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {       // 解析BeanDefinition
        // BeanDefinitionHolder 为BeanDefinition的封装类，用它来向IOC容器注册
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
                // 向IOC容器注册解析得到的BeanDefinition
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
            // 注册完成后，发送消息
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}
```

这里我们可以看到最终是由BeanDefinitionParserDelegate来对Element进行解析的， 具体的在实现如下：

public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, @Nullable BeanDefinition containingBean) {
		String id = ele.getAttribute(ID_ATTRIBUTE);
		String nameAttr = ele.getAttribute(NAME_ATTRIBUTE);

​		List<String> aliases = new ArrayList<>();
​		if (StringUtils.hasLength(nameAttr)) {
​			String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, MULTI_VALUE_ATTRIBUTE_DELIMITERS);
​			aliases.addAll(Arrays.asList(nameArr));
​		}

​		String beanName = id;
​		if (!StringUtils.hasText(beanName) && !aliases.isEmpty()) {
​			beanName = aliases.remove(0);
​			if (logger.isTraceEnabled()) {
​				logger.trace("No XML 'id' specified - using '" + beanName +
​						"' as bean name and " + aliases + " as aliases");
​			}
​		}

​		if (containingBean == null) {
​			checkNameUniqueness(beanName, aliases, ele);
​		}
​         
​        // 这里会触发对Bean的详细解析过程
​		AbstractBeanDefinition beanDefinition = parseBeanDefinitionElement(ele, beanName, containingBean);
​		if (beanDefinition != null) {
​			if (!StringUtils.hasText(beanName)) {
​				try {
​					if (containingBean != null) {
​						beanName = BeanDefinitionReaderUtils.generateBeanName(
​								beanDefinition, this.readerContext.getRegistry(), true);
​					}
​					else {
​						beanName = this.readerContext.generateBeanName(beanDefinition);
​						// Register an alias for the plain bean class name, if still possible,
​						// if the generator returned the class name plus a suffix.
​						// This is expected for Spring 1.2/2.0 backwards compatibility.
​						String beanClassName = beanDefinition.getBeanClassName();
​						if (beanClassName != null &&
​								beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() &&
​								!this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
​							aliases.add(beanClassName);
​						}
​					}
​					if (logger.isTraceEnabled()) {
​						logger.trace("Neither XML 'id' nor 'name' specified - " +
​								"using generated bean name [" + beanName + "]");
​					}
​				}
​				catch (Exception ex) {
​					error(ex.getMessage(), ele);
​					return null;
​				}
​			}
​			String[] aliasesArray = StringUtils.toStringArray(aliases);
​			return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
​		}

​		return null;
​	}

我们可以看到在parseBeanDefinitionElement方法中，有对Bean的详细解析：

```java
public AbstractBeanDefinition parseBeanDefinitionElement(
			Element ele, String beanName, @Nullable BeanDefinition containingBean) {

		this.parseState.push(new BeanEntry(beanName));

		String className = null;
         //获取ClassName
		if (ele.hasAttribute(CLASS_ATTRIBUTE)) {
			className = ele.getAttribute(CLASS_ATTRIBUTE).trim();
		}
		String parent = null;
		if (ele.hasAttribute(PARENT_ATTRIBUTE)) {
			parent = ele.getAttribute(PARENT_ATTRIBUTE);
		}

		try {
            // 生成BeanDefinition对象
			AbstractBeanDefinition bd = createBeanDefinition(className, parent);

			parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
			bd.setDescription(DomUtils.getChildElementValueByTagName(ele, DESCRIPTION_ELEMENT));
            // 对各种Bean属性进行解析
			parseMetaElements(ele, bd);
			parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
			parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
            // 构造函数的解析
			parseConstructorArgElements(ele, bd);
			parsePropertyElements(ele, bd);
			parseQualifierElements(ele, bd);

			bd.setResource(this.readerContext.getResource());
			bd.setSource(extractSource(ele));

			return bd;
		}
		catch (ClassNotFoundException ex) {
			error("Bean class [" + className + "] not found", ele, ex);
		}
		catch (NoClassDefFoundError err) {
			error("Class that bean class [" + className + "] depends on not found", ele, err);
		}
		catch (Throwable ex) {
			error("Unexpected failure during bean definition parsing", ele, ex);
		}
		finally {
			this.parseState.pop();
		}

		return null;
	}
```

到此我们找到了具体解析Bean的地方，最终得到了BeanDefinitionHolder对象，对各种元素的解析我们不进入分析，有兴趣的话可以自己看。下面是时序图：

![BeanDefinition的载入和解析](C:\Users\jrjiakun\Desktop\BeanDefinition的载入和解析.png)

