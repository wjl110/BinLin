package com.show.other;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 文章表
 * </p>
 *
 * @author 916202420@qq.com
 * @since 2022-04-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel("搜索条件模型")
public class SearchCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "当前页不能小于1")
    @ApiModelProperty(value = "当前页", name = "currentPage", dataType = "int", example = "1", required = true)
    private Integer currentPage;

    @Min(value = 1, message = "页面大小不能小于1")
    @Max(value = 10, message = "页面大小不能超过10")
    @ApiModelProperty(value = "当前页", name = "pageSize", dataType = "int", example = "5", required = true)
    private Integer pageSize;

    @NotBlank(message = "搜索文本不能为空")
    @ApiModelProperty(value = "搜索文本", name = "searchText", dataType = "String", example = "测试", required = true)
    private String searchText;

    @ApiModelProperty(value = "其他", name = "other", dataType = "Map", required = false)
    private Map<String, String> other;

    public SearchCondition setCurrentPage(Integer currentPage) {
        this.currentPage = (currentPage == null || currentPage < 1) ? 1 : currentPage;
        return this;
    }

    public SearchCondition setPageSize(Integer pageSize) {
        this.pageSize = (pageSize == null || pageSize < 1) ? 1 : (pageSize > 10 ? 10 : pageSize);
        return this;
    }
    public Integer getStartIndex() {
        return (currentPage - 1) * pageSize;
    }
}